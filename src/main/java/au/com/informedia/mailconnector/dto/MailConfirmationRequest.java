package au.com.informedia.mailconnector.dto;

import au.com.informedia.mailconnector.persistance.domain.document.BookingDocument;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MailConfirmationRequest extends OutBoundRequest {

    private String id;
    private MailConfirmationRequest.Advisor advisor;
    private MailConfirmationRequest.TransportOption transportOption;
    private MailConfirmationRequest.Dealer dealer;
    private MailConfirmationRequest.Customer customer;
    private MailConfirmationRequest.OperationList operations;
    private MailConfirmationRequest.VehicleModel vehicleModel;
    private String extraWorks;
    private String currencyCode;
    private String totalPrice;
    private LocalDate date;
    private LocalDateTime bookingTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String icsUrl;
    private String advisorComments;

    @Data
    public static class Advisor {
        private Long id;
        private String name;
    }

    @Data
    public static class Customer {
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNo;
        private String extraNotes;
    }

    @Data
    public static class Operation {
        private String id;
        private String name;
        private String price;
    }

    @Data
    public static class TransportOption {

        private Long id;
        private String name;
    }

    @Data
    public static class Dealer {
        private Long dealerId;
        private String dealerName;
        private String address;
        private String email;
        private String phone;
        private String timeZone;
        private String timeZoneString;
        private float longitude;
        private float latitude;
    }

    @Data
    public static class VehicleModel {

        private String modelId;
        private String model;

    }

    public String getIcsUrlString(String awsFolderUrl){
        return awsFolderUrl + id + ".ics";
    }

    @Data
    public static class OperationList {
        private Operation service;
        private List<Operation> repairs;
    }

}
