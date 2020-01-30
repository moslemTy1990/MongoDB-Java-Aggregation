import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.BucketOptions;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
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
        long startTime = Long.parseLong("1571842449859");    //selecting a period
        long finishTime = Long.parseLong("1574438049859");
        String mysignal = "InterfaceType.InjectionUnits.InjectionUnit_1.TemperatureZones.TemperatureZone_1.ActualTemperature";
        String IdManfc = "144-12";

        createBucketTimePeriod(startTime,finishTime);  //fill up the time period bucket

        MongoCollection<Document> coll = mongoClient.getDatabase(dbs.get(0)).getCollection(dbc.get(2));
//---------------------------------------------------------------------------------------------------------------
  /*      Bson startTimeUnwind = match( gt("lastTimestamp",startTime));
        Bson endTimeUnwind= match(lte("lastTimestamp",finishTime));
        Bson idFilterUnwind = match(eq("id", IdManfc));
        Bson unwind = unwind("$signals");
        Bson filterSignalUnwind = match(eq("signals.signal",  mysignal));
        Bson groupUnwind=group("$signals.signal",
                avg("Average","$signals.value"),
                stdDevSamp("STD","$signals.value"),
                max("Max","$signals.value"),
                min("Min","$signals.value")
        );

       List<Bson> pipeline =  asList(idFilterUnwind,
                startTimeUnwind,
                endTimeUnwind,
                unwind,
                filterSignalUnwind,
                groupUnwind
        );

        List<Document> results = coll.aggregate(pipeline).into(new ArrayList<Document>());
        for (Document Document : results) {
            System.out.println(Document);
        }  */
//-------------------------------------------------------------------------------------
        Bson startTimeBucket = match( gte("lastTimestamp",startTime));
        Bson endTimeBucket= match(lte("lastTimestamp",finishTime));
        Bson idFilterBucket = match(eq("id", IdManfc));
        Bson unwindBucket = unwind("$signals");
        Bson filterSignalUnBucket = match(eq("signals.signal", mysignal));

        Bson Bucket=bucket("$lastTimestamp", timeBucketList, new BucketOptions()
                .defaultBucket("sum")
                .output(min("id","$id"),
                        min("signal","$signals.signal"),
                        avg("Average",Document.parse("{$toDouble: \"$signals.value\"}")),
                        stdDevSamp("STD",Document.parse("{$toDouble: \"$signals.value\"}")),
                        max("Max",Document.parse("{$toDouble: \"$signals.value\"}")),
                        min("Min",Document.parse("{$toDouble: \"$signals.value\"}"))
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

        resultbuckt.forEach(f -> saveToMariaDB(
                                                   ConvertTimeStamp((long)f.get("_id")) ,
                                                    f.get("id").toString() ,
                                                    f.get("signal").toString(),
                                                    (Double) f.get("Min"),
                                                    (Double) f.get("Max"),
                                                    (Double)f.get("Average"),
                                                    (Double)f.get("STD")
                                                 ));
    }

    /*
    * SaveToMariaDB is a method that filters a specific signal
    * and saves the Signal and its value to the database
    */
    private void saveToMariaDB(Date timeStamp , String id,String signal,Double avg, Double STD, Double Max, Double Min) {

            try{
                MariaDB.InsertDataIntoRelationalDB(timeStamp, id,signal,avg,STD,Max,Min);
            }catch(Exception e){
                System.out.println(e);
            }
         }

    /*Converting the Milisec time to the date time and returning the value as string*/
    private Date  ConvertTimeStamp(Long timeStamp){
        Date currentDate = new Date(timeStamp);
      return currentDate;
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