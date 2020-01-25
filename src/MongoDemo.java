import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MongoDemo {

    public static void main(String[] args) {

        MyMethods Myconnection = new MyMethods(); //Instanciating the class of main methods
        Myconnection.CheckConnection();    // Checking if the connection works or not
       Myconnection.getCollection();   // Printing the Collection Names
       Myconnection.FilterTemp();  //Printing and showing an example in Filters
    }

}
