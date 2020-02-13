import java.io.IOException;

public class mainClass {

    public static void main(String[] args) throws IOException {
        //---------------------------
        PropertiesClass prop = new PropertiesClass();
        prop.setProperies();
        //---------------------------
       mongoClientClass Client = new mongoClientClass(); //Instanciating the class of main methods
       Client.CheckConnection();    // Checking if the connection works or not
       Client.getCollection();   // Printing the Collection Names
       Client.FilterTemp();  //Printing and showing an example in Filters
    }
}
