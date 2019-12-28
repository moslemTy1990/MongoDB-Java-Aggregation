import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class MariaClass {

    public  static Connection CheckMariaConnection() {
        Connection conn = null;
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/test?user=root&passwors=");
          //  JOptionPane.showMessageDialog(null, "Success");
          return conn;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error" + e);
            return null;
        }
    }

    public void InsertDataIntoRelationalDB(String String1,String String2) throws Exception  {
        try {
         Connection conn=   CheckMariaConnection();
            PreparedStatement doInsert = conn.prepareStatement("INSERT INTO exporteddata (Signal1,Val) " +
                                            "VALUES ('" + String1 + "','" + String2 + "') ");
            doInsert.executeUpdate();
            System.out.println("Insert Completed");
        } catch (Exception e){
            System.out.println(e);
        }
    }
}



