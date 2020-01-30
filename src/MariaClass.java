import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class MariaClass {

    public  static Connection CheckMariaConnection() {
        Connection conn = null;
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/mongodbdata?user=root&passwors=");
          //  JOptionPane.showMessageDialog(null, "Success");
          return conn;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error" + e);
            return null;
        }
    }  // method to check the connection of MariaDB
    Connection conn=   CheckMariaConnection();
    public void InsertDataIntoRelationalDB(String timeStamp,String id,String signal,Double avg,Double std,Double Max,Double Min) throws Exception  {
        try {

            PreparedStatement doInsert = conn.prepareStatement("INSERT INTO exporteddata (timeStamp,id,average,std,max,min) " +
                                            "VALUES ('" + timeStamp + "','" + id + "'," + avg + "," + std +"," + Max+"," + Min +") ");
            doInsert.executeUpdate();
          //  System.out.println("Insert Completed");

        } catch (Exception e){
            System.out.println(e);
        }
    }
}



