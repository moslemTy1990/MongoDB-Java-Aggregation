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
            System.out.println("MariaDb Connection Stablished");
          return conn;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error" + e);
            return null;
        }
    }  // method to check the connection of MariaDB
    Connection conn=   CheckMariaConnection();
    public void InsertDataIntoRelationalDB(String timeStamp,String id,String Nsignal,Double avg,Double std,Double Max,Double Min) throws Exception  {

        String myQuery ="INSERT INTO exporteddata (timeStamp,id,signalName,average,std,max,min) " +
                        "VALUES ('"
                                    + timeStamp + "','"
                                    + id + "','"
                                    + Nsignal +  "',"
                                    + avg + ","
                                    + std + ","
                                    + Max + ","
                                    + Min  +
                                ")";

        try {

            PreparedStatement doInsert = conn.prepareStatement(myQuery);
            doInsert.executeUpdate();
        } catch (Exception e){
            System.out.println(e);
        }
    }
}
/*
create table mongodbdata.exporteddata
(
	timeStamp varchar(30) null,
	id varchar(30) null,
	signal varchar(300) null,
	average double null,
	std double null,
	max double null,
	min double null
);
* */



