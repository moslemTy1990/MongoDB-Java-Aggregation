import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.util.*;

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
    mariaDB MariaDB = new mariaDB();


    public static List<String> gettechsiganls() throws IOException {
        List<String> techSignals = new ArrayList() ;
        List<String> kuraIdTechignals = new ArrayList<>();
        HashMap<String,String> hashMappedSignals = new HashMap<String,String>();
        techSignals =getTechSignals();
        hashMappedSignals=getHashMapKeyAndValue();
        try {

            for (Object obj : hashMappedSignals.entrySet()) {
                Map.Entry<String, String> entry = (Map.Entry) obj;
                String key = entry.getKey();
                if (techSignals.contains(entry.getValue())==true) {
                    kuraIdTechignals.add(key);
                }
            }
        }catch (Exception e){
            System.out.println("Error : " + e.toString());
        }
        return  kuraIdTechignals;
    }

    public static List<Document> getCollectionasset(String plantName) throws IOException {

        String plant = mongoAdminProp.getFilterField_plant();
        String customerField=mongoAdminProp.getCustomer() ;
        String sigit = mongoAdminProp.getSigit();

        MongoCollection<Document> coll = mongoAdmin.getDatabase(dbsAdmin.get(0)).getCollection(dbcAdmin.get(3));
        Bson customer = match(eq(customerField, sigit));
        Bson plantname = match(eq(plant, plantName));;
        List<Document>  assetIDs = coll.aggregate(
                                                    asList( customer
                                                            ,plantname
                                                    )).into(new ArrayList<Document>());
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

        String category = mongoAdminProp.getCategory();
        String technological = mongoAdminProp.getTechnological();
        String openplatid = mongoAdminProp.getOpenplatid();

        MongoCollection<Document> coll = mongoAdmin.getDatabase(dbsAdmin.get(0)).getCollection(dbcAdmin.get(0));
        Bson filterPlant = eq(category, technological);
        List<Document> listTech = new ArrayList<>();
        List<String> techSignalArray =  new ArrayList<>();

        coll.find(filterPlant)
                .iterator()
                .forEachRemaining(listTech::add);

        for ( Document doc: listTech) {
            techSignalArray.add(((String)doc.get(openplatid)));
        }

        return techSignalArray;
    }

    public static HashMap<String,String> getHashMapKeyAndValue() throws IOException {

        String signals = mongoAdminProp.getSignals();
        String array = mongoAdminProp.getArray();
        String openplatID = mongoAdminProp.getOpenplatid();
        String kuraID = mongoAdminProp.getKuraid();

        HashMap<String,String> kuraIdPlatIdHashMap = new HashMap<String,String>();

        MongoCollection<Document> coll = mongoAdmin.getDatabase(dbsAdmin.get(0)).getCollection(dbcAdmin.get(4));
        Bson filterType = type(signals, array);
        List<Document> listHash = new ArrayList<>();

        coll.find(and(filterType))
                .iterator()
                .forEachRemaining(listHash::add);

        for ( Document doc: listHash) {
            List<Document> arr = (List<Document>) doc.get("signals");
            arr.forEach(e ->  kuraIdPlatIdHashMap.put((String)e.get(kuraID),(String)e.get(openplatID)) );
        }

        while(kuraIdPlatIdHashMap.keySet().remove(null));
        while(kuraIdPlatIdHashMap.values().remove(null));

       return kuraIdPlatIdHashMap;
    }

    public  void  getPladIdandDescription() throws IOException {
           mongoAdminProp = new PropertiesClass();
           mongoAdminProp.setProperies();
           myAdminMongoUri = new MongoClientURI(mongoAdminProp.getAdminMongoUri());
           mongoAdmin = new MongoClient(myAdminMongoUri);
           List<Document> listTech = new ArrayList<>();

           CheckMongoAdminConnection();    // Checking if the connection works or not
           getAdminCollection();   // Printing the Collection Names

           String category = mongoAdminProp.getCategory();
           String technological = mongoAdminProp.getTechnological();
           String openplatid = mongoAdminProp.getOpenplatid() ;
           String description =mongoAdminProp.getDescription();

           MongoCollection<Document> coll = mongoAdmin.getDatabase(dbsAdmin.get(0)).getCollection(dbcAdmin.get(0));
           Bson filterPlant = eq(category, technological);

           coll.find(filterPlant)
                .iterator()
                .forEachRemaining(listTech::add);

           listTech.forEach(f ->
                saveOpenPalandDescToMariaDB(
                        f.get(openplatid).toString() ,
                        f.get(description).toString()
                ));
    }

    private  void saveOpenPalandDescToMariaDB(String openplatid, String description) {
        try{
            MariaDB.InsertOpenPalandDescToMariaDB(openplatid,description);
        }catch(Exception e){
            System.out.println(e);
        }
    }
}

