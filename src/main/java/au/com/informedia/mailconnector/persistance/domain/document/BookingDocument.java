package au.com.informedia.mailconnector.persistance.domain.document;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "booking")
public class BookingDocument {
    @Id
    private String id;
    private Advisor advisor;
    private TransportOption transportOption;
    private Dealer dealer;
    private Customer customer;
    private OperationList operations;
    private VehicleModel vehicleModel;
    private String extraWorks;
    private String currencyCode;
    private Float totalPrice;
    @Enumerated(EnumType.ORDINAL)
    private BookingStatus status;
    private LocalDate date;
    private LocalDateTime bookingTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String icsUrl;
    private BookingType type;
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
        private Float price;
        private OperationCategory category;
    }

    @Data
    public static class TransportOption {
        private Long id;
        private String name;


    }

    public enum BookingStatus {
        Cancelled,
        Booked,
        Completed,
        Missed
    }

    public enum BookingType {
        Appointment,
        Walking
    }

    @Data
    public static class Dealer {
        private Long dealerId;
        private String dealerName;
        private String address;
        private String email;
        private String phone;
        private String timeZone;
        private float longitude;
        private float latitude;
    }

    @Data
    public static class VehicleModel {
        private String modelId;
        private String model;
        private String modelCode;
        private String series;
        private String regNo;
        private String vin;
        private Long mileage;
        private String imgUrl;
        private Long year;
    }

    public String getIcsUrlString(String awsFolderUrl){
        return awsFolderUrl + id + ".ics";
    }

    @Data
    public static class OperationList {
        private Operation service;
        private List<Operation> repairs;
    }

    @Data
    public static class OperationCategory {
        private String id;
        private String name;
        private String categoryId;

        public OperationCategory() {}

    }
}




