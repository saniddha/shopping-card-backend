package au.com.informedia.mailconnector.util;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class DateUtil {

    public static Date converStringToDate(String strDate, String format) throws ParseException {
        SimpleDateFormat sdf1 = new SimpleDateFormat(format);
        return sdf1.parse(strDate);
    }

    public static LocalDateTime convertToDealerTime(LocalDateTime utcTime, ZoneId dealerZone){
        ZoneId utcZone = ZoneId.of("UTC");
        return getParsedTime(utcTime, utcZone, dealerZone);
    }
    public static LocalDateTime convertToUtc(LocalDateTime dealerTime, ZoneId dealerZone){
        ZoneId utcZone = ZoneId.of("UTC");
        return getParsedTime(dealerTime, dealerZone, utcZone);
    }

    public static String getMongoDBStringFromDate(LocalDate date){
        return date.atTime(0, 0, 0).atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }

    private static LocalDateTime getParsedTime(LocalDateTime timeToParse, ZoneId fromZone, ZoneId toZone) {
        return timeToParse.atZone(fromZone).withZoneSameInstant(toZone).toLocalDateTime();
    }
}
