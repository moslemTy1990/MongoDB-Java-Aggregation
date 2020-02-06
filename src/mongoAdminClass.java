import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.BucketOptions;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Filters.eq;
import static java.util.Arrays.asList;

public class mongoAdminClass {
    private static PropertiesClass mongoAdminProp;
    private static List<String> dbsAdmin = new ArrayList<>();
    private static List<String> dbcAdmin = new ArrayList<>();
    private static MongoClientURI myAdminMongoUri;
    private static MongoClient mongoAdmin;

    public static void main(String[] args) throws IOException {
        getCollectionasset();
        getTechSignals();
        getHashMapKeyAndValue();


    }






    public static List<Document> getCollectionasset() throws IOException {
        mongoAdminProp = new PropertiesClass();
        mongoAdminProp.setProperies();
        myAdminMongoUri = new MongoClientURI(mongoAdminProp.getAdminMongoUri());
        mongoAdmin = new MongoClient(myAdminMongoUri);

        CheckMongoAdminConnection();    // Checking if the connection works or not
        getAdminCollection();   // Printing the Collection Names

        String idCambiano =mongoAdminProp.getFilterPlant_val_cambiano();
        String plant = mongoAdminProp.getFilterField_plant();
        String manufacturer=mongoAdminProp.getManufacturer_field();
        String gmb =mongoAdminProp.getGmb_val() ;

        MongoCollection<Document> coll = mongoAdmin.getDatabase(dbsAdmin.get(0)).getCollection(dbcAdmin.get(3));
        Bson filterPlant = match(eq(plant, idCambiano));
        Bson filtermanufacture = match(eq(manufacturer, gmb));

        List<Document>  assetIDs = coll.aggregate(asList( filterPlant
                                                        ,filtermanufacture
                                                         )).into(new ArrayList<Document>());
        for (Document Document : assetIDs) {
            System.out.println(Document);
        }
        return assetIDs;
    }

    public static void CheckMongoAdminConnection() {
        getDatabaseNames();
        System.out.println("The name of the data base: " + dbsAdmin.get(0));
    }
    public static void getAdminCollection() {
        System.out.println("the name of the collections of the data base: " + dbsAdmin.get(0) + " are " + getColectionNames());
    }
    private static List<String> getDatabaseNames() {
        MongoCursor<String> dbsCursor = mongoAdmin.listDatabaseNames().iterator();
        while (dbsCursor.hasNext()) {
            dbsAdmin.add(dbsCursor.next());
        }
        return dbsAdmin;
    }
    //List the collection of the database and put them into an array
    private static List<String> getColectionNames() {
        MongoCursor<String> myAdmincollections = mongoAdmin.getDatabase(dbsAdmin.get(0)).listCollectionNames().iterator();
        while (myAdmincollections.hasNext()) {
            dbcAdmin.add(myAdmincollections.next());
        }
        return dbcAdmin;
    }

    public static List<String> getTechSignals() throws IOException {
     //   mongoAdminProp = new PropertiesClass();
     //   mongoAdminProp.setProperies();
      //  myAdminMongoUri = new MongoClientURI(mongoAdminProp.getAdminMongoUri());
      //  mongoAdmin = new MongoClient(myAdminMongoUri);

       // CheckMongoAdminConnection();    // Checking if the connection works or not
       // getAdminCollection();   // Printing the Collection Names

        String category = mongoAdminProp.getCategory();
        String technological = mongoAdminProp.getTechnological();

        MongoCollection<Document> coll = mongoAdmin.getDatabase(dbsAdmin.get(0)).getCollection(dbcAdmin.get(0));
        Bson filterPlant = eq(category, technological);
        List<Document> listTech = new ArrayList<>();
        List<String> techSignalArray =  new ArrayList<>();

        coll.find(filterPlant)
                .iterator()
                .forEachRemaining(listTech::add);

        for ( Document doc: listTech) {
          //  List<Document> arr = (List<Document>) doc.get("signals");
            techSignalArray.add(((String)doc.get("openplatid")));
        }
        for ( String item:techSignalArray
             ) {
            System.out.println(item);
        }

     /*   for (Document Document : listTech) {
            System.out.println(Document);
        }*/
        return techSignalArray;
    }

    public static HashMap<String,String> getHashMapKeyAndValue() throws IOException {

        HashMap<String,String> kuraIdPlatIdHashMap = new HashMap<String,String>();

        MongoCollection<Document> coll = mongoAdmin.getDatabase(dbsAdmin.get(0)).getCollection(dbcAdmin.get(4));
        Bson filterType = type("signals", "array");
       // Bson filterXpr = expr(Document.parse("{ $gt: [ { $size: '$signals' }, 0 ] }"));
        List<Document> listHash = new ArrayList<>();

        coll.find(and(filterType))
                .iterator()
                .forEachRemaining(listHash::add);

        for ( Document doc: listHash) {
            List<Document> arr = (List<Document>) doc.get("signals");
            arr.forEach(e ->  kuraIdPlatIdHashMap.put((String)e.get("kuraid"),(String)e.get("openplatid")) );
        }
      //  System.out.println(kuraIdPlatIdHashMap.size());

        while(kuraIdPlatIdHashMap.keySet().remove(null));
        while(kuraIdPlatIdHashMap.values().remove(null));

       // System.out.println(kuraIdPlatIdHashMap.size());

       /* for (Object obj : kuraIdPlatIdHashMap.entrySet()) {
            Map.Entry<String, String> entry = (Map.Entry) obj;
            System.out.print("Key: " + entry.getKey());
            System.out.println(", Value: " + entry.getValue());
        }
*/
       return kuraIdPlatIdHashMap;
    }

}

