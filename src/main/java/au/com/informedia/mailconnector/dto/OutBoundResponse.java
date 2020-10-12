package au.com.informedia.mailconnector.dto;

import lombok.Data;

@Data
public class OutBoundResponse {

    public OutBoundResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public OutBoundResponse() {
    }

    private String status;
    private String message;
}
