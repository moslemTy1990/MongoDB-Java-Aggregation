import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.BucketOptions;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static java.util.Arrays.asList;

public  class mongoClientClass {
    private static PropertiesClass mongoClientProp = new PropertiesClass();
    private MongoClientURI MyMongoUri = new MongoClientURI(mongoClientProp.getClientMongoUri_sigit()); // connecition string
    private MongoClient mongoClient = new MongoClient(MyMongoUri);
    private List<String> dbs = new ArrayList<>();
    private List<String> dbc = new ArrayList<>();
    private List<Long> timeBucketList = new ArrayList<>();
    mariaDB MariaDB = new mariaDB();
    mongoAdminClass adminclass;

    // checking the mongoConnection
    public void CheckConnection() {
        getDatabaseNames();
        System.out.println("The name of the data base: " + dbs.get(0));
    }

    // geting the collection on a list
    public void getCollection() {
        System.out.println("the name of the collections of the data base: " + dbs.get(0) + " are " + getColectionNames());
    }

    public void FilterTemp() throws IOException {
        long startTime = Long.parseLong(mongoClientProp.getStartTime_val());    //selecting a period
        long finishTime = Long.parseLong(mongoClientProp.getFinishTime_val());

        createBucketTimePeriod(startTime, finishTime);  //fill up the time period bucket

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
        String unwindSignal = mongoClientProp.getSignalArray_exp();
        String id = mongoClientProp.getFilterField_id();
        String signals_signalfield = mongoClientProp.getSignals_signalfield();
        String lastTimeStamp = mongoClientProp.getTimeStampField_lst();
        String groupByTimeStamp = mongoClientProp.getTimeStamp_lst_exp();
        String sum = mongoClientProp.getDefaultBucket_opt();
        String toDouble_value = mongoClientProp.getToDouble_value();
        String standardDevField = mongoClientProp.getStandardDevField();
        String maxVal = mongoClientProp.getMaxVal();
        String minVal = mongoClientProp.getMinVal();
        String averageVal = mongoClientProp.getAverageField();
        String signal = mongoClientProp.getSignalField();
        String signals_signalfield_exp = mongoClientProp.getSignals_signalfield_exp();
        String id_exp = mongoClientProp.getId_exp();
        String timeID = mongoClientProp.getTimeID();


        String tempSignal = mongoClientProp.getSignal_val();

        List<Document> IdBuckets = adminclass.getCollectionasset();


        for (int i = 0; i < IdBuckets.size(); i++) {
           String idManfc = IdBuckets.get(i).get("id").toString();

            Bson startTimeBucket = match(gte(lastTimeStamp, startTime));
            Bson endTimeBucket = match(lte(lastTimeStamp, finishTime));
            Bson idFilterBucket = match(eq(id, idManfc));
            Bson unwindBucket = unwind(unwindSignal);
            Bson filterSignalUnBucket = match(eq(signals_signalfield, tempSignal));

            Bson Bucket = bucket(groupByTimeStamp, timeBucketList, new BucketOptions()
                    .defaultBucket(sum)
                    .output(min(id, id_exp),
                            min(signal, signals_signalfield_exp),
                            avg(averageVal, Document.parse(toDouble_value)),
                            stdDevSamp(standardDevField, Document.parse(toDouble_value)),
                            max(maxVal, Document.parse(toDouble_value)),
                            min(minVal, Document.parse(toDouble_value))
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
                    ConvertTimeStamp((long) f.get(timeID)),
                    f.get(id).toString(),
                    f.get(signal).toString(),
                    (Double) f.get(minVal),
                    (Double) f.get(maxVal),
                    (Double) f.get(averageVal),
                    (Double) f.get(standardDevField)
            ));
        }
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
        MongoCursor<String> myClientcollections = mongoClient.getDatabase(dbs.get(0)).listCollectionNames().iterator();
        while (myClientcollections.hasNext()) {
            dbc.add(myClientcollections.next());
        }
        return dbc;
    }
   }