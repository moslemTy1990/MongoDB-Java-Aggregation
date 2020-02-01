import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class mariaDB {
private static PropertiesClass mariadbProp = new PropertiesClass();
    public  static Connection CheckMariaConnection() {
        Connection conn = null;
        try {
            Class.forName(mariadbProp.getMariaClassDriver());
            conn = DriverManager.getConnection(mariadbProp.getmariaDBUrl());
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

        String myQuery = mariadbProp.getInsert_Into_mongodb_data() +
                        "VALUES ('"
                                    + device_key + "','"
                                    + signal_code +  "','"
                                    + dateHour(date_key,mariadbProp.getDateType_day()) + "','"
                                    + dateHour(date_key,mariadbProp.getDateType_hour()) + "',"
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
    private String dateHour(Date date_key,String type){
        String returntime;
        String strDateFormat = mariadbProp.getStrDateFormat();
        String strHourFormat = mariadbProp.getStrHourFormat();
        String strDateHourFormat = mariadbProp.getStrDateHourFormat();
        SimpleDateFormat date = new SimpleDateFormat(strDateFormat);
        SimpleDateFormat hour = new SimpleDateFormat(strHourFormat);
        SimpleDateFormat fulldate = new SimpleDateFormat(strDateHourFormat);

        switch (type) {
            case "H":
                returntime = hour.format(date_key);
                break;
            case "D":
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



