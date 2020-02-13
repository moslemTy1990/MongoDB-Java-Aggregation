import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesClass {
    private static String mariaClassDriver;
    private static String mariaDBUrl;
    private static String strDateFormat;
    private static String strHourFormat;
    private static String strDateHourFormat;
    private static String dateType_day;
    private static String dateType_hour;
    private static String adminMongoUri;
    private static String filterField_plant;
    private static String clientMongoUri_sigit;
    private static String startTime_val;
    private static String finishTime_val;
    private static String timeStampField_lst;
    private static String filterField_id;
    private static String signalArray_exp;
    private static String signals_signalfield;
    private static String timeStamp_lst_exp;
    private static String defaultBucket_opt;
    private static String id_exp;
    private static String signalField;
    private static String signals_signalfield_exp;
    private static String averageField;
    private static String toDouble_value;
    private static String standardDevField;
    private static String maxVal;
    private static String minVal;
    private static String insert_Into_mongodb_data;
    private  static String insert_Into_OpenPlatIDandDesc;
    private static String timeID;
    private static String technological;
    private static String category;
    private static String openplatid;
    private static String description;
    private static String kuraid;
    private static String array;
    private static String signals;
    private static String customer;
    private static String sigit;
    public static void setProperies() throws IOException {
        Properties prop = readPropertiesFile("config.properties");
        insert_Into_mongodb_data = prop.getProperty("insert_Into_mongodb_data");
        insert_Into_OpenPlatIDandDesc = prop.getProperty("insert_Into_OpenPlatIDandDesc");
        mariaClassDriver =  prop.getProperty("mariaClassDriver");
        mariaDBUrl =  prop.getProperty("mariaDBUrl");
        strDateFormat =  prop.getProperty("strDateFormat");
        strHourFormat =  prop.getProperty("strHourFormat");
        strDateHourFormat =  prop.getProperty("strDateHourFormat");
        dateType_day =  prop.getProperty("dateType_day");
        dateType_hour =  prop.getProperty("dateType_hour");
        adminMongoUri =  prop.getProperty("adminMongoUri");
        filterField_plant =  prop.getProperty("filterField_plant");
        clientMongoUri_sigit =  prop.getProperty("clientMongoUri_sigit");
        startTime_val =  prop.getProperty("startTime_val");
        finishTime_val =  prop.getProperty("finishTime_val");
        timeStampField_lst =  prop.getProperty("timeStampField_lst");
        filterField_id =  prop.getProperty("filterField_id");
        signalArray_exp =  prop.getProperty("signalArray_exp");
        signals_signalfield =  prop.getProperty("signals_signalfield");
        timeStamp_lst_exp =  prop.getProperty("timeStamp_lst_exp");
        defaultBucket_opt =  prop.getProperty("defaultBucket_opt");
        id_exp =  prop.getProperty("id_exp");
        signalField =  prop.getProperty("signalField");
        signals_signalfield_exp =  prop.getProperty("signals_signalfield_exp");
        averageField =  prop.getProperty("averageVal");
        toDouble_value =  prop.getProperty("toDouble_value");
        standardDevField =  prop.getProperty("standardDevField");
        maxVal =  prop.getProperty("maxVal");
        minVal =  prop.getProperty("minVal");
        timeID = prop.getProperty("timeID");
        category = prop.getProperty("category");
        technological = prop.getProperty("technological");
        openplatid =prop.getProperty("openplatid");
        description=prop.getProperty("description");
        array=prop.getProperty("array");
        signals=prop.getProperty("signals");
        kuraid =prop.getProperty("kuraid");
        customer=prop.getProperty("customer");
        sigit =prop.getProperty("sigit");

    }
    public static Properties readPropertiesFile(String fileName) throws IOException {
        FileInputStream fis = null;
        Properties prop = null;
        try {
            fis = new FileInputStream(fileName);
            prop = new Properties();
            prop.load(fis);
        } catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            fis.close();
        }
        return prop;
    }

    public  String getCustomer() {
        return customer;
    }
    public  String getSigit() {
        return sigit;
    }
    public  String getKuraid() {
        return kuraid;
    }
    public  String getArray() {
        return array;
    }
    public  String getSignals() {
        return signals;
    }
    public  String getInsert_Into_OpenPlatIDandDesc() {
        return insert_Into_OpenPlatIDandDesc;
    }
    public  String getOpenplatid() {
        return openplatid;
    }
    public  String getDescription() {
        return description;
    }
    public String getTechnological() {
        return technological;
    }
    public String getCategory() {
        return category;
    }
    public String getTimeID() {
        return timeID;
    }
    public String getInsert_Into_mongodb_data(){
      return insert_Into_mongodb_data;
    }
    public String getMariaClassDriver() {
        return mariaClassDriver;
    }
    public String getmariaDBUrl() {
        return mariaDBUrl;
    }
    public String getStrDateFormat() {
        return strDateFormat;
    }
    public String getStrHourFormat() {
        return strHourFormat;
    }
    public String getStrDateHourFormat() {
        return strDateHourFormat;
    }
    public String getDateType_day() {
        return dateType_day;
    }
    public String getDateType_hour() {
        return dateType_hour;
    }
    public String getAdminMongoUri() {
        return adminMongoUri;
    }
    public String getFilterField_plant() {
        return filterField_plant;
    }
    public String getClientMongoUri_sigit() {
        return clientMongoUri_sigit;
    }
    public String getStartTime_val() {
        return startTime_val;
    }
    public String getFinishTime_val() {
        return finishTime_val;
    }
    public String getTimeStampField_lst() {
        return timeStampField_lst;
    }
    public String getFilterField_id() {
        return filterField_id;
    }
    public String getSignalArray_exp() {
        return signalArray_exp;
    }
    public String getSignals_signalfield() {
        return signals_signalfield;
    }
    public String getTimeStamp_lst_exp() {
        return timeStamp_lst_exp;
    }
    public String getDefaultBucket_opt() {
        return defaultBucket_opt;
    }
    public String getId_exp() {
        return id_exp;
    }
    public String getSignalField() {
        return signalField;
    }
    public String getSignals_signalfield_exp() {
        return signals_signalfield_exp;
    }
    public String getAverageField() {
        return averageField;
    }
    public String getToDouble_value() {
        return toDouble_value;
    }
    public String getStandardDevField() {
        return standardDevField;
    }
    public String getMaxVal() {
        return maxVal;
    }
    public String getMinVal() {
        return minVal;
    }
}
