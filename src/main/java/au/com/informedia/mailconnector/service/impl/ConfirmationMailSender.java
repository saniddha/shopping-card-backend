package au.com.informedia.mailconnector.service.impl;

import au.com.informedia.mailconnector.config.ConfigConst;
import au.com.informedia.mailconnector.controller.EmailControler;
import au.com.informedia.mailconnector.dto.MailConfirmationRequest;
import au.com.informedia.mailconnector.dto.OutBoundRequest;
import au.com.informedia.mailconnector.dto.OutBoundResponse;
import au.com.informedia.mailconnector.exception.InternalServerErrorException;
import au.com.informedia.mailconnector.persistance.domain.document.BookingDocument;
import au.com.informedia.mailconnector.persistance.repository.nosql.BookingRepository;
import au.com.informedia.mailconnector.service.EmailSendService;
import au.com.informedia.mailconnector.util.DateUtil;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mongodb.annotations.Beta;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ConfirmationMailSender implements EmailSendService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmationMailSender.class);
    final private ConfigConst configConst;


    public ConfirmationMailSender(ConfigConst configConst) {
        this.configConst = configConst;
    }


    @Override
    public OutBoundResponse sendEmail(MailConfirmationRequest request) {

        try {
            sendMessageByTemplateId(request);
        } catch (UnirestException e) {
            LOGGER.error("Error occured when sending the mail to => " + e.getStackTrace());
            throw new InternalServerErrorException("Couldn't connect to mail server", false);
        }
        return new OutBoundResponse("Success", "Successfully Send the Mail");
    }

    private JsonNode sendMessageByTemplateId(MailConfirmationRequest bookingDocument) throws UnirestException {

        String dealerLocation = getDirectionString(bookingDocument.getDealer().getLongitude(), bookingDocument.getDealer().getLatitude(),configConst.getDealerLocation());
        String operations = getListedServices(bookingDocument.getOperations(), bookingDocument.getCurrencyCode());
        String timeRage = getTimeRage(bookingDocument.getStartTime(), bookingDocument.getEndTime(), bookingDocument.getDealer().getTimeZone());
        String notification = getNotifications(bookingDocument.getStartTime(), timeRage, configConst.getMailNotofication());
        String formatedDay = getFormattedDate(bookingDocument.getStartTime());
        HttpResponse<JsonNode> request = Unirest.post(configConst.getMailGunUrl() + configConst.getDomainName() + configConst.getMailGunUrlAppend())
                .basicAuth(configConst.getMailGunUserName(), configConst.getMailGunKey())
                .field("from", configConst.getSystemMail())
                .field("to", bookingDocument.getCustomer().getEmail())
                .field("subject", getSubject(configConst.getSubject(), bookingDocument.getId()))
                .field("template", configConst.getTemplateName())
                .field("o:tracking", "False")
                .field("h:X-Mailgun-Variables", operations)
                .field("v:appointment_no", bookingDocument.getId())
                .field("v:dealer_name", bookingDocument.getDealer().getDealerName())
                .field("v:dealer_email", bookingDocument.getDealer().getEmail())
                .field("v:dealer_address", bookingDocument.getDealer().getAddress())
                .field("v:dealer_contact", bookingDocument.getDealer().getPhone())
                .field("v:time_zone", bookingDocument.getDealer().getTimeZone())
                .field("v:dealer_location", dealerLocation)
                .field("v:add_to_calendar", bookingDocument.getIcsUrl())
                .field("v:appointment_time", timeRage)
                .field("v:appointment_date", formatedDay)
                .field("v:booking_notes", bookingDocument.getCustomer().getExtraNotes())
                .field("v:notification", notification)
                .field("v:vehicle_model", bookingDocument.getVehicleModel().getModel())
                .field("v:transport_option", bookingDocument.getTransportOption().getName())
                .field("v:advisor", bookingDocument.getAdvisor().getName())
                .field("v:total_price", bookingDocument.getTotalPrice())
                .field("v:cancel_url", configConst.getBookingCancelUrl() + bookingDocument.getId())
                .asJson();

        return request.getBody();
    }

    private String getSubject(String subject, String appointmentId){

        return subject.replace("{bookingId}",appointmentId);
    }

    private String getDirectionString(float longitude, float latitude, String confDirection){
        return confDirection.replace("{latitude}",formatFloat(latitude,ConfigConst.locationDecimal)).replace("{longitude}",formatFloat(longitude,6));
    }

    private String getListedServices(MailConfirmationRequest.OperationList operationList, String currency){

        List<MailConfirmationRequest.Operation> operations = new ArrayList<>();
        MailConfirmationRequest.Operation service = operationList.getService();
        if(service != null){
            operations.add(service);
        }else{
            MailConfirmationRequest.Operation noSche = new MailConfirmationRequest.Operation();
            noSche.setName("No Scheduled Service");
            noSche.setId("0");
            noSche.setPrice("");
            operations.add(noSche);
        }
        operations.addAll(operationList.getRepairs());
        String operationsHtml = "{\"operations\": [";
        for (MailConfirmationRequest.Operation operation: operations) {
            String price = operation.getPrice();
            String temp = "{\"name\":\" "+ operation.getName()+"\", \"price\": \" "+ price+"\" },";
            operationsHtml += temp;
        }
        operationsHtml =   operationsHtml.substring(0, operationsHtml.length() - 1);
        operationsHtml +="]}";
        return operationsHtml;
    }

    private String getFormattedDate(LocalDateTime time){

        String strtDayName = time.getDayOfWeek().name();
        String strtMonth = time.getMonth().name();
        String strtDate = String.valueOf(time.getDayOfMonth());
        String year = String.valueOf(time.getYear());
        return "on " + strtDayName + ", " + strtMonth + " " +strtDate + ", " + year;
    }

    private String getNotifications(LocalDateTime startTime, String timeRage, String confNotification) {

        String strtDayName = startTime.getDayOfWeek().name();
        String strtMonth = startTime.getMonth().name();
        String strtDate = String.valueOf(startTime.getDayOfMonth());

        return confNotification.replace("{timeRage}",timeRage).replace("{strtDayName}",strtDayName).replace("{strtMonth}",strtMonth).replace("{trtDate}",strtDate);
    }

    private String formatFloat(float value, int points){
        return String.format(Locale.US, "%."+points+"f", value);
    }

    private String getTimeRage(LocalDateTime strtTime, LocalDateTime endTime, String dealerTimeZone){

        LocalDateTime convertedStartTime = DateUtil.convertToDealerTime(strtTime, ZoneId.of(dealerTimeZone));
        LocalDateTime convertEndTime = DateUtil.convertToDealerTime(endTime, ZoneId.of(dealerTimeZone));

        return  formatTime(convertedStartTime) + " to " + formatTime(convertEndTime);
    }

    private String formatTime (LocalDateTime time){

        String minutes = String.valueOf(time.getMinute());
        String formattedTime = "";
        int hours = time.getHour();
        if (hours >= 12){
            formattedTime = String.valueOf(hours) + ":" + minutes + " pm";
        } else {
            formattedTime = String.valueOf(hours) + ":" + minutes + " am";
        }
        return formattedTime;
    }
}
