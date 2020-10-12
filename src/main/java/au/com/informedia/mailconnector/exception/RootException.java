package au.com.informedia.mailconnector.exception;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class RootException extends RuntimeException {

    private HttpStatus status;
    private String message;
    private String statusCode;
    private boolean visible;
    private JsonNode data;

    RootException(String message, HttpStatus status, String statusCode, boolean visible) {
        super(message);
        this.message = message;
        this.status = status;
        this.statusCode = statusCode;
        this.visible = visible;
    }

    RootException(String message, HttpStatus status, String statusCode, boolean visible, JsonNode data) {
        super(message);
        this.message = message;
        this.status = status;
        this.statusCode = statusCode;
        this.visible = visible;
        this.data = data;
    }

}
