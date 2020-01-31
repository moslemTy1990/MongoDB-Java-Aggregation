import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCursor;

import java.util.ArrayList;
import java.util.List;

public class mongoAdminClass {
    private static MongoClientURI myAdminMongoUri = new MongoClientURI("mongodb://etlReader:just3Xtr4ct@10.10.101.117:27017/admin");
    private static MongoClient mongoAdmin = new MongoClient(myAdminMongoUri);
    private static List<String> dbsAdmin = new ArrayList<>();
    private static List<String> dbcAdmin = new ArrayList<>();


    public static void main(String[] args) {

        CheckMongoAdminConnection();    // Checking if the connection works or not
        getAdminCollection();   // Printing the Collection Names



    }

    public static void CheckMongoAdminConnection() {
        getDatabaseNames();
        System.out.println("The name of the data base: " + dbsAdmin.get(0));
    }
    public static void getAdminCollection() {
        System.out.println("the name of the collections of the data base: " + dbsAdmin.get(0) + "is" + getColectionNames());
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

