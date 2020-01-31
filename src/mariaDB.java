import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class mariaDB {

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
    public void InsertDataIntoRelationalDB(Date date_key, String device_key, String signal_code,
                                            Double min_value, Double max_value, Double avg_value, Double std_value) throws Exception  {

        String myQuery ="INSERT INTO mongodb_data (device_key,signal_code,date_key,time_key,min_value,max_value,avg_value,std_value) " +
                        "VALUES ('"
                                    + device_key + "','"
                                    + signal_code +  "','"
                                    + dateHour(date_key,'D') + "','"
                                    + dateHour(date_key,'H') + "',"
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

    /*
     *In this function i split the date and time in seprate field.
     */
    private String dateHour(Date date_key,Character type){
        String returntime;
        String strDateFormat = "yyyy/MM/dd";
        String strHourFormat = "HH:mm:ss";
        String strDateHourFormat = "yyyy/MM/dd HH:mm:ss";
        SimpleDateFormat date = new SimpleDateFormat(strDateFormat);
        SimpleDateFormat hour = new SimpleDateFormat(strHourFormat);
        SimpleDateFormat fulldate = new SimpleDateFormat(strDateHourFormat);

        switch (type) {
            case 'H':
                returntime = hour.format(date_key);
                break;
            case 'D':
                returntime = date.format(date_key);
                break;
            default:
                returntime = fulldate.format(date_key);
        }
        return returntime;
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



