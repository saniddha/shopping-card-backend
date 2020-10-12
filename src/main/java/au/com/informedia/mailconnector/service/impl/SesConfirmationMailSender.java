package au.com.informedia.mailconnector.service.impl;

import au.com.informedia.mailconnector.config.ConfigConst;
import au.com.informedia.mailconnector.controller.EmailControler;
import au.com.informedia.mailconnector.dto.MailConfirmationRequest;
import au.com.informedia.mailconnector.dto.OutBoundRequest;
import au.com.informedia.mailconnector.dto.OutBoundResponse;
import au.com.informedia.mailconnector.dto.common.MailType;
import au.com.informedia.mailconnector.exception.InternalServerErrorException;
import au.com.informedia.mailconnector.persistance.domain.document.BookingDocument;
import au.com.informedia.mailconnector.persistance.repository.nosql.BookingRepository;
import au.com.informedia.mailconnector.service.EmailSendService;
import au.com.informedia.mailconnector.util.DateUtil;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.model.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.SendTemplatedEmailRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
public class SesConfirmationMailSender implements EmailSendService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SesConfirmationMailSender.class);
    final private ConfigConst configConst;

    public SesConfirmationMailSender( ConfigConst configConst) {
        this.configConst = configConst;
    }

    @Override
    public OutBoundResponse sendEmail(MailConfirmationRequest request) {

        LOGGER.debug("Sending the mail for the booking => "  + request);

        String subject = configConst.getSesConfirmSubject();
        boolean cancellation = false;
        if (request.getEmailType() == MailType.AppointmentCancellation.ordinal()){
            subject = configConst.getSesCancelSubject();
            cancellation = true;
        } else if (request.getEmailType() == MailType.AppointmentUpdate.ordinal()){
            subject = configConst.getSesEditSubject();
        }
        try {
            AWSCredentials credentials = new BasicAWSCredentials(
                    configConst.getSesAccesskey(),
                    configConst.getSesSecretkey()
            );
            AmazonSimpleEmailService client =
                    AmazonSimpleEmailServiceClientBuilder.standard()
                            .withCredentials(new AWSStaticCredentialsProvider(credentials))
                            .withRegion(Regions.US_EAST_1).build();
            SendEmailRequest mailRequest = new SendEmailRequest()
                    .withDestination(
                            new Destination().withToAddresses(request.getCustomer().getEmail()))
                    .withMessage(new Message()
                            .withBody(new Body()
                                    .withHtml(new Content()
                                            .withCharset("UTF-8").withData(sendMessageByTemplateId(request, cancellation)))
                                    )
                            .withSubject(new Content()
                                    .withCharset("UTF-8").withData(getSubject(subject, request.getId()))))
                    .withSource(configConst.getSystemMail())
                    ;
            client.sendEmail(mailRequest);
        } catch (Exception ex) {
            LOGGER.error("Error occured when sending the mail with booking id " + request + " => " + Arrays.toString(ex.getStackTrace()));
            throw new InternalServerErrorException("Couldn't connect to mail server", false);
        }
        LOGGER.info("Successfully sending the mail for the request => " + request);
        return new OutBoundResponse("Success", "Successfully Send the Mail");
    }

    private String sendMessageByTemplateId(MailConfirmationRequest bookingDocument, Boolean cancellation) {

        LOGGER.debug("Creating the mail html page for the booking confirmation = > " + bookingDocument);
        String dealerLocation = getDirectionString(bookingDocument.getDealer().getLongitude(), bookingDocument.getDealer().getLatitude(),configConst.getDealerLocation());
        String operations = getListedServices(bookingDocument.getOperations(), bookingDocument.getCurrencyCode());
        String timeRage = getTimeRage(bookingDocument.getStartTime(), bookingDocument.getEndTime(), bookingDocument.getDealer().getTimeZone());
        String notification = getNotifications(bookingDocument.getStartTime(), timeRage, configConst.getMailNotofication());

        LocalDateTime date= DateUtil.convertToDealerTime(bookingDocument.getStartTime(), ZoneId.of(bookingDocument.getDealer().getTimeZone()));
        String formatedDay = getFormattedDate(date);
        String bookingNotes = bookingDocument.getCustomer().getExtraNotes();

//        Creating html page for confirmation, inline html is needed for SES
        String htmlBody = "<!DOCTYPE html><html lang=\"en\"><head> <meta charset=\"utf-8\"> <meta content=\"width=700\" name=\"viewport\"> <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"> <meta name=\"x-apple-disable-message-reformatting\"> <link href=\"https://fonts.googleapis.com/css?family=Rubik\" rel=\"stylesheet\"> <title></title> <style>html, body{font-family: Helvetica; height: 100%; width: 100%;}*{-ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;}hr{border-bottom: 1px solid rgba(0, 20, 39, 0.12); border-top: none; margin: 25px 0;}h1{font-size: 24px; color: rgba(15, 16, 17, 0.87);}h2{color: rgba(15, 16, 17, 0.87); font-size: 18px}h3{font-size: 16px; color: rgba(15, 16, 17, 0.87);}a{color: #18A2D5;}p{font-size: 16px; line-height: 20px; margin: 0 0 5px 0; color: rgba(15, 16, 17, 0.87); text-align: left;}.button{border: 2px solid #18A2D5; color: #18A2D5; border-radius: 25px; padding: 8px 15px 8px 45px; background-color: #ffffff; font-size: 16px; line-height: 16px; font-weight: bold; position: relative; cursor: pointer; display: inline-block; text-decoration: none;}.button img{position: absolute; left: 14px; top: 5px;}img{-ms-interpolation-mode: bicubic;}.service-repairs tr td{padding: 10px 0; border-top: 1px solid rgba(15, 16, 17, 0.06); width: 75%; color: rgba(15, 16, 17, 0.65);}.service-repairs tr:last-child td{font-weight: bold; color: rgba(15, 16, 17, 0.87);}.row-section1{display: flex;}</style></head> " +
                "<body width=\"100%\" style=\"margin: 0; padding: 0; background-color: #CCCCCC; \"> <div class=\"inner-body\" style=\"width: 850px; margin: 30px auto; background-color: #ffffff; padding: 30px 0 30px 0;\"> <div style=\"width: 100%; background-color: #ffffff;\"> <table role=\"presentation\" style=\"width: 680px; margin: 0 auto; padding: 24px 28px; border: 1px solid rgba(0, 20, 39, 0.12); border-radius: 8px;\" class=\"email-container\"> <tr> <td> <section style=\"margin-bottom: 20px;\"> <h1 style=\"font-weight: bold; text-align: center; margin: 0 0 10px 0 ; line-height: 20px; counter-reset: rgba(15, 16, 17, 0.87)\"> Booking confirmation</h1> <p style=\"font-size: 14px; text-align: center; line-height: 20px; margin: 0;\">Appointment no. <span style=\"font-weight: bold;\">";
        htmlBody+= bookingDocument.getId() + "</span></p></section> <hr> <section style=\"position: relative; padding-left: 100px;\"> <span style=\"position: absolute; left: 22px;\"><img src=\"https://ifm-connect-dev.s3-ap-southeast-1.amazonaws.com/nissan-logo.svg\"></span> <div style=\"text-align: left;\"> <h2 style=\"margin: 0 0 6px 0;\">";
        htmlBody+= bookingDocument.getDealer().getDealerName() + "</h2> <p style=\"color: rgba(15, 16, 17, 0.65);\">"+ bookingDocument.getDealer().getAddress() +"</p><p style=\"color: rgba(15, 16, 17, 0.65);\">Email: <a href=\"mailto:";
        htmlBody+= bookingDocument.getDealer().getEmail() + "\">"+ bookingDocument.getDealer().getEmail() +"</a></p><p style=\"color: rgba(15, 16, 17, 0.65);\">Phone:"+  bookingDocument.getDealer().getPhone() +"</p></div></section> <hr> <table style=\"margin-bottom: 30px; width: 100%;\"> <tr> <td style=\"position: relative; padding-left: 35px;\" valign=\"top\"> <span style=\"position: absolute; left: 0; top: 0;\"><img src=\"https://ifm-connect-dev.s3-ap-southeast-1.amazonaws.com/calender-icon.svg\"></span> <p style=\"color: rgba(15, 16, 17, 0.38); font-size: 14px; margin-bottom: 10px;\"> Appointment date and time</p><p>";
        htmlBody+= timeRage + "</p><p>"+ formatedDay +"</p>";
        if (!cancellation) {
            htmlBody+= "<a href=\"" + bookingDocument.getIcsUrl()+ "\" class=\"button\" target=\"_blank\"><img src=\"https://ifm-connect-dev.s3-ap-southeast-1.amazonaws.com/calender-view-icon.svg\"/>Add to Calendar</a> ";
        }

        htmlBody+="</td><td style=\"position: relative; padding-left: 35px;\" valign=\"top\"> <span style=\"position: absolute; left: 0; top: 0;\"><img src=\"https://ifm-connect-dev.s3-ap-southeast-1.amazonaws.com/location-icon.svg\"></span> <p style=\"color: rgba(15, 16, 17, 0.38); font-size: 14px; margin-bottom: 10px\"> Dealer address</p><p>";
        htmlBody+= bookingDocument.getDealer().getAddress() +"</p>";
        if (!cancellation){
            htmlBody+= "<a href=\""+ dealerLocation +"\" class=\"button\" target=\"_blank\"><img src=\"https://ifm-connect-dev.s3-ap-southeast-1.amazonaws.com/map-icon.svg\"/>Get direction</a>";
        }
        htmlBody+= "</td></tr></table> ";

        if (!cancellation) {
            htmlBody+="<section style=\"background: rgba(2, 104, 177, 0.08); margin-bottom: 30px; padding: 10px 10px 10px 40px; position: relative; border-radius: 4px;\"> <span style=\"position: absolute; left: 10px; top: 10px;\"><img src=\"https://ifm-connect-dev.s3-ap-southeast-1.amazonaws.com/info-icon.svg\"/></span> <h3 style=\"color: #0268B1; font-size: 14px; margin: 0 0 5px 0;\">All times are in ";
            htmlBody+= bookingDocument.getDealer().getTimeZoneString()+".</h3> <p style=\"color: #0268B1; font-size: 14px\">";
            htmlBody+= notification +"</p></section>";
        }

        htmlBody+= "<table style=\"margin-bottom: 25px; width: 100%;\"> <tr> <td valign=\"top\"> <p style=\"color: rgba(15, 16, 17, 0.38);\">Your vehicle</p><h3 style=\"margin: 0 0 5px 0\">";
        htmlBody+= bookingDocument.getVehicleModel().getModel() +"</h3> </td><td valign=\"top\"> <span style=\"margin-bottom: 15px;\"> <p style=\"color: rgba(15, 16, 17, 0.38);\">Transport option</p><h3 style=\"margin: 0 0 5px 0\">";
        htmlBody+= bookingDocument.getTransportOption().getName() + "</h3> </span> <span> <p style=\"color: rgba(15, 16, 17, 0.38);\">Your advisor</p><h3 style=\"margin: 0 0 5px 0\">";
        htmlBody+= bookingDocument.getAdvisor().getName() +"</h3> </span> </td></tr></table> <section style=\"margin-bottom: 20px;\"> <h2>Service and repairs</h2> <table class=\"service-repairs\" style=\"width: 100%;\">";
        htmlBody+= operations;
        htmlBody+= "<tr> <td>Total</td><td>";
        htmlBody+= bookingDocument.getTotalPrice() + "</td></tr></table> </section>";

        if (bookingNotes != null && !bookingNotes.equals("")){
            htmlBody+= "<section> <i style=\"display: block; margin-bottom: 10px; color: rgba(15, 16, 17, 0.87);\">Notes</i> <p style=\"font-size: 14px; margin: 0;\">";
            htmlBody+=bookingNotes +"</p></section>";
        }
        htmlBody+="<hr> ";

        if (!cancellation) {
            htmlBody+="<section style=\"margin-bottom: 15px;\"> <h2 style=\"margin: 0 0 10px 0;\">Cancellation</h2> <p style=\"margin: 0 0 12px 0;\">For cancellation, please contact us or plase click the button below.</p><a href=\"";
            htmlBody+=configConst.getBookingCancelUrl() + bookingDocument.getId() + "\" class=\"button\" target=\"_blank\" style=\"padding: 8px 15px;\">Cancel appointment</a> </section>";
        }
        htmlBody += "<section> <p style=\"color: rgba(15, 16, 17, 0.65); margin: 0; text-align: right; font-size: 12px; line-height: 16px;\"> *Aditional work may increase your total price.</p></section> </td></tr></table> </div></div></body></html>";

        return htmlBody;
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
        if(operationList.getService() != null) {
            operations.add(service);
        }
        operations.addAll(operationList.getRepairs());

        StringBuilder operationsHtml = new StringBuilder();
        for (MailConfirmationRequest.Operation operation: operations) {
            String price = operation.getPrice();
            operationsHtml.append("<tr> <td>").append(operation.getName()).append("</td><td>").append(price).append("</td></tr>");
        }
        return operationsHtml.toString();
    }

    private String getFormattedDate(LocalDateTime date){


        String strtDayName = date.getDayOfWeek().name();
        String strtMonth = date.getMonth().name();
        String strtDate = String.valueOf(date.getDayOfMonth());
        String year = String.valueOf(date.getYear());
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        return time.format(formatter);
    }
}
