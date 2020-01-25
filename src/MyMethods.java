import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BucketAutoOptions;
import com.mongodb.client.model.BucketOptions;
import com.mongodb.client.model.Filters;
import org.bson.BSON;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.imageio.plugins.tiff.TIFFDirectory;
import java.util.*;

import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.ascending;
import static java.util.Arrays.asList;

public  class MyMethods {
    private MongoClientURI MyMongoUri = new MongoClientURI("mongodb://etlReader:just3Xtr4ct@10.10.101.113:27017/sigit_his");
    private MongoClient mongoClient = new MongoClient(MyMongoUri);
    private List<String> dbs = new ArrayList<>();
    private List<String> dbc = new ArrayList<>();
    private List<Long> timeBucketList = new ArrayList<>();
    MariaClass MariaDB = new MariaClass();

    // checking the mongoConnection
    public void CheckConnection() {
        getDatabaseNames();
        System.out.println("The name of the data base: " + dbs.get(0));
    }

    // geting the collection on a list
    public void getCollection() {
        System.out.println("the name of the collections of the data base: " + dbs.get(0) + "is" + getColectionNames());
    }

    public void FilterTemp() {
        List<Document> resultList = new ArrayList<>();
        long startTime = Long.parseLong("1571842449859");    //selecting a period
        long finishTime = Long.parseLong("1574438049859");
        String mysignal = "InterfaceType.InjectionUnits.InjectionUnit_1.TemperatureZones.TemperatureZone_1.ActualTemperature";
        String IdManfc = "144-12";

        createBucketTimePeriod(startTime,finishTime);  //fill up the time period bucket

        MongoCollection<Document> coll = mongoClient.getDatabase(dbs.get(0)).getCollection(dbc.get(2));
        Bson filterID = Filters.eq("id", IdManfc) ;   //Filter based on a specific manufacturure
        Bson startTimeFilter= gte("lastTimestamp",startTime);  // Filter based on a period
        Bson endTimeFilter= lte("lastTimestamp",finishTime);  // Filter based on a period
        Bson filterType = Filters.type("signals", "array");     // Consider the array Signals
        Bson filterTypeXpr =  Filters.elemMatch("signals", Filters.eq("signal",
                mysignal));
        // Filtering a specific signal of the array

        // in this part i apply the filter
        coll.find(and(startTimeFilter,endTimeFilter,filterID,filterType, filterTypeXpr))
                .limit(50)//Todo remove this limit , bucket data hour by hour , put into maria db each record representing each hour
                // store avg, store standard deviation, each in a record
                .iterator()
                .forEachRemaining(resultList::add);

        for ( Document doc1: resultList) {        // in this part i get the value and the signal content and store in the maria db through a method
            List<Document> arr = (List<Document>) doc1.get("signals");
            arr.forEach(e -> SaveToMariaDB(e.get("value").toString(),e.get("signal").toString()));
        }
/*
       FindIterable<Document>  doc=coll.find(and(startTimeFilter,endTimeFilter,filterID,startTimeFilter,filterType, filterTypeXpr)).limit(5);
        for (Document Document : doc) {
            System.out.println(Document);
        }
 */

        Bson startTimeUnwind = match( gt("lastTimestamp",startTime));
        Bson endTimeUnwind= match(lte("lastTimestamp",finishTime));
        Bson idFilterUnwind = match(eq("id", IdManfc));
        Bson unwind = unwind("$signals");
        Bson filterSignalUnwind = match(eq("signals.signal",
                mysignal));

        Bson groupUnwind=group("$signals.signal",
                sum("Summation","$signals.value"),
                avg("Average","$signals.value"),
                stdDevSamp("STD","$signals.value"),
                max("Max","$signals.value"),
                min("Min","$signals.value")
        );

        Bson lim = limit(20);
        Bson ascSort = sort(ascending("lastTimestamp"));

        List<Bson> pipeline = asList(idFilterUnwind,
                startTimeUnwind,
                endTimeUnwind,
                unwind,
                filterSignalUnwind,
                groupUnwind
                //   lim,
        );

        List<Document> results = coll.aggregate(pipeline).into(new ArrayList<Document>());
        for (Document Document : results) {
            System.out.println(Document);
        }

//-------------------------------------------------------------------------------------
        Bson startTimeBucket = match( gt("lastTimestamp",startTime));
        Bson endTimeBucket= match(lte("lastTimestamp",finishTime));
        Bson idFilterBucket = match(eq("id", IdManfc));
        Bson unwindBucket = unwind("$signals");
        Bson filterSignalUnBucket = match(eq("signals.signal",
                mysignal));

        Bson Bucket=bucket("$lastTimestamp", timeBucketList, new BucketOptions()
                .defaultBucket("sum")
                .output( min("id","$id"),
                        min("signal","$signals.signal"),
                        sum("Summation","$signals.value"),
                        avg("Average","$signals.value"),
                        stdDevSamp("STD","$signals.value"),
                        max("Max","$signals.value"),
                        min("Min","$signals.value")
                )
        );

        List<Document> resultbuckt = coll.aggregate(asList(startTimeBucket,
                endTimeBucket,
                idFilterBucket,
                unwindBucket,
                filterSignalUnBucket,
                Bucket
        )).into(new ArrayList<Document>());
        for (Document Document : resultbuckt) {
            System.out.println(Document);
        }
/*
        resultbuckt.forEach(f -> System.out.println(
                                                  " Time " + ConvertTimeStamp(f.get("_id")) +
                                                  " Sum " + f.get("Summation") +
                                                  " Avg " + f.get("Average") +
                                                  " STD " + f.get("STD") +
                                                  " Max " + f.get("Max") +
                                                  " Min " + f.get("Min")
                                                    ));*/
    }

    /*
    * SaveToMariaDB is a method that filters a specific signal
    * and saves the Signal and its value to the database
    */
    private void SaveToMariaDB(String val , String sig) {  //TODO date and time
        if(sig.equals("InterfaceType.InjectionUnits.InjectionUnit_1.TemperatureZones.TemperatureZone_1.ActualTemperature")) {
            System.out.println(val + " " + sig );
            try{
                MariaDB.InsertDataIntoRelationalDB(sig, val);
            }catch(Exception e){
                System.out.println(e);
            }
         }
    }

    /*
    *CreateBucetTimePeriod is a method that fills up the BucketList
    * in order to do filter aggregation based on a time period
    */
    private void createBucketTimePeriod(Long startTime, Long finishTime) {

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        Calendar endTemp =Calendar.getInstance();

        startDate.setTimeInMillis(startTime);
        endDate.setTimeInMillis(finishTime);
        endTemp.setTimeInMillis(finishTime);

        try {

            timeBucketList.add(startDate.getTimeInMillis());
            startDate.set(Calendar.MINUTE,00);
            startDate.set(Calendar.SECOND,00);
            endTemp.set(Calendar.MINUTE,00);
            endTemp.set(Calendar.SECOND,00);

            while(  startDate.getTimeInMillis() < endTemp.getTimeInMillis() ) {
                startDate.add(Calendar.HOUR, 1);
                timeBucketList.add(startDate.getTimeInMillis());
            }
            timeBucketList.add(endDate.getTimeInMillis());
            System.out.println("Time Bucket Loaded");
        }catch (Exception e){
            System.out.println("Error : " + e.toString());
        }
    }

    //list the database names and put them into an array
    private List<String> getDatabaseNames() {
        MongoCursor<String> dbsCursor = mongoClient.listDatabaseNames().iterator();
        while (dbsCursor.hasNext()) {
            dbs.add(dbsCursor.next());
        }
        return dbs;
    }

    //List the collection of the database and put them into an array
    private List<String> getColectionNames() {
        MongoCursor<String> Mycollections = mongoClient.getDatabase(dbs.get(0)).listCollectionNames().iterator();
        while (Mycollections.hasNext()) {
            dbc.add(Mycollections.next());
        }
        return dbc;
    }
   }