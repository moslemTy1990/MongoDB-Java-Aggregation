import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.*;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.*;

public  class MyMethods {
    public MongoClientURI MyMongoUri = new MongoClientURI("mongodb://etlReader:just3Xtr4ct@10.10.101.113:27017/sigit_his");
    public MongoClient mongoClient = new MongoClient(MyMongoUri);
    public List<String> dbs = new ArrayList<>();
    public List<String> dbc = new ArrayList<>();
    MariaClass MariaDB = new MariaClass();
    public void CheckConnection() {
        getDatabaseNames();
        System.out.println("The name of the data base: " + dbs.get(0));
    }   // checking the mongoConnection

    public void getCollection() {
        System.out.println("the name of the collections of the data base: " + dbs.get(0) + "is" + getColectionNames());
    }      // geting the collection on a list

    public void FilterTemp() {


            long StartPeriod = Long.parseLong("1571842449859");    //selecting a period
            long FinishTime = Long.parseLong("1571842519709");

        MongoCollection<Document> coll = mongoClient.getDatabase(dbs.get(0)).getCollection(dbc.get(2));
        Bson filterID = Filters.eq("id", "144-12") ;   //Filtering a specific manufacturure
        Bson TimeFilter= gte("lastTimestamp",StartPeriod);  // Filtering a period
        Bson filterType = Filters.type("signals", "array");     // Consider the array Signals
        Bson filterTypeXpr = expr(Document.parse("{ $gt: [ { $size: '$signals' }, 0 ] }")); // If signal has ni value avoid it.

        List<Document> list = new ArrayList<>();   // in this part i apply the filter 
        coll.find(and(TimeFilter,filterID,filterType, filterTypeXpr))
                .limit(5)
                .iterator()
                .forEachRemaining(list::add);

        for ( Document doc1: list) {        // in this part i get the value and store in the maria db through a method
            List<Document> arr = (List<Document>) doc1.get("signals");
            //arr.forEach(e -> System.out.println(e.get("value")));
            arr.forEach(e -> SaveToMariaDB(e.get("value").toString(),e.get("signal").toString()));
        }

       FindIterable<Document>  doc=coll.find(and(TimeFilter,filterID,TimeFilter,filterType, filterTypeXpr)).limit(5);
        for (Document Document : doc) {
            System.out.println(Document);
        }

/*
       Bson Myfilter=Filters.and(
                            Filters.eq("id", "144-12"),
                                  Filters.gte("lastTimestamp",StartPeriod ),
                                  Filters.elemMatch("signals",Filters.ne("value","0.0")),
                                  Filters.elemMatch("signals",Filters.ne("value","0")),
                                 Filters.elemMatch("signals",Filters.eq("signal",
                                                "InterfaceType.Jobs.ActiveJob.NumCavities"))
                     );

        FindIterable<Document>  doc=coll.find(Myfilter).limit(1);
        for (Document Document : doc) {
            System.out.println(Document);
        }
*/    // My previous code
                /*
        TOD try to avoing result with empty signal and try to do a filter with specific signal
        the get the values of the signal with start time and stop time
        */   //ToDo
    }

    public void SaveToMariaDB(String val , String sig) {
      //  if(sig.equals("InterfaceType.InjectionUnits.InjectionUnit_1.TemperatureZones.TemperatureZone_2.NominalTemperature")) {
            System.out.println(val + " " + sig );
            try{
                MariaDB.InsertDataIntoRelationalDB(sig, val);
            }catch(Exception e){
                System.out.println(e);
            }

      //  }

    }   // this is the method to store to database

    public List<String> getDatabaseNames() {
        MongoCursor<String> dbsCursor = mongoClient.listDatabaseNames().iterator();
        while (dbsCursor.hasNext()) {
            dbs.add(dbsCursor.next());
        }
        return dbs;
    }
    public List<String> getColectionNames() {
        MongoCursor<String> Mycollections = mongoClient.getDatabase(dbs.get(0)).listCollectionNames().iterator();
        while (Mycollections.hasNext()) {
            dbc.add(Mycollections.next());
        }
        return dbc;
    }
   }