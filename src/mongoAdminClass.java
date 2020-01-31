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
    private static MongoClientURI myAdminMongoUri = new MongoClientURI("mongodb://etlReader:just3Xtr4ct@10.10.101.117:27017/admin");
    private static MongoClient mongoAdmin = new MongoClient(myAdminMongoUri);
    private static List<String> dbsAdmin = new ArrayList<>();
    private static List<String> dbcAdmin = new ArrayList<>();


    public static void main(String[] args) throws IOException {

        CheckMongoAdminConnection();    // Checking if the connection works or not
        getAdminCollection();   // Printing the Collection Names
        getCollectionasset();



            Properties prop = readPropertiesFile("config.properties");
            System.out.println("username: "+ prop.getProperty("username"));
            System.out.println("password: "+ prop.getProperty("password"));





    }

    public static Properties readPropertiesFile(String fileName) throws IOException {
        FileInputStream fis = null;
        Properties prop = null;
        try {
            fis = new FileInputStream(fileName);
            prop = new Properties();
            prop.load(fis);
        } catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            fis.close();
        }
        return prop;
    }

    public static List<Document> getCollectionasset(){
        String IdPlant = "cambiano";
        MongoCollection<Document> coll = mongoAdmin.getDatabase(dbsAdmin.get(0)).getCollection(dbcAdmin.get(3));
        Bson idFilterBucket = match(eq("plant", IdPlant));


        List<Document>  assetIDs = coll.aggregate(asList( idFilterBucket)).into(new ArrayList<Document>());
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

