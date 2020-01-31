public class mainClass {

    public static void main(String[] args) {

        mongoClientClass Myconnection = new mongoClientClass(); //Instanciating the class of main methods
        Myconnection.CheckConnection();    // Checking if the connection works or not
       Myconnection.getCollection();   // Printing the Collection Names
       Myconnection.FilterTemp();  //Printing and showing an example in Filters
    }

}
