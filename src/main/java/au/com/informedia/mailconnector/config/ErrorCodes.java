package au.com.informedia.mailconnector.config;

public class ErrorCodes {

    //    Generic Error Codes
    public static String MYSQL_DB_CONNECTION_ERROR = "GE1000";
    public static String MYSQL_DB_DATA_EMPTY = "GE1001";
    public static String MONGO_DB_CONNECTION_ERROR = "GE1002";
    public static String MONGO_DB_DATA_EMPTY = "GE1003";

    //    Availability Error Codes
    public static String ADVISOR_NOT_AVAILABLE = "AE1100";
    public static String TRANSPORT_OPTION_NOT_AVAILABLE = "AE1101";
    public static String TIMESLOT_OPTION_NOT_AVAILABLE_FOR_ADVISOR = "AE1102";
    public static String TIMESLOT_OPTION_NOT_AVAILABLE_FOR_ANY_ADVISOR = "AE1001";

    //  Validation Error Codes
    public static String NOT_VALID_ADVISOR= "VE1500";
    public static String NOT_VALID_TRANSPORT_OPTION = "VE1501";
    public static String NOT_VALID_OPERATION = "VE1502";
    public static String NOT_VALID_TIMESLOT = "VE1503";
    public static String NOT_VALID_DEALER = "VE1504";
}
