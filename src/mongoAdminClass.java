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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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
       // TTTTTTTTTTTTTTTTTTOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
       mongoAdminProp = new PropertiesClass();
        mongoAdminProp.setProperies();
      //  myAdminMongoUri = new MongoClientURI(mongoAdminProp.getAdminMongoUri());
      //  mongoAdmin = new MongoClient(myAdminMongoUri);

        String id = mongoAdminProp.getFilterField_id();
        String signal = mongoAdminProp.getSignalField();

        List<Document> resultbuckt = getCollectionasset();


        for(int i = 0; i < resultbuckt.size() ; i++){
            System.out.println(resultbuckt.get(i).get("id"));
        }
    }

    public static List<Document> getCollectionasset() throws IOException {
      //  mongoAdminProp = new PropertiesClass();
      //  mongoAdminProp.setProperies();
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

        List<Document>  assetIDs = coll.aggregate(asList( filterPlant,filtermanufacture)).into(new ArrayList<Document>());
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
}

