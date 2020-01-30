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
    public void InsertDataIntoRelationalDB(String timeStamp,String device_key,String signal_code,Double min_value,Double max_value,Double avg_value,Double std_value) throws Exception  {

        String myQuery ="INSERT INTO mongodb_data (device_key,signal_code,min_value,max_value,avg_value,std_value) " +
                        "VALUES ('"
                                 //   + timeStamp + "','"
                                    + device_key + "','"
                                    + signal_code +  "',"
                                    + min_value + ","
                                    + max_value + ","
                                    + avg_value + ","
                                    + std_value  +
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
create table mongodbdata.MongoDB_Data
(
	technical_key bigint auto_increment,
	device_key varchar(30) null,
	signal_code varchar(30) null,
	date_key date null,
	time_key time null,
	min_value DOUBLE null,
	max_value DOUBLE null,
	avg_value DOUBLE null,
	std_value DOUBLE null,
	primary key (technical_key)
);


* */



