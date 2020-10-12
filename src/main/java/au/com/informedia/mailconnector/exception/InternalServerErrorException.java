package au.com.informedia.mailconnector.exception;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends RootException {

    private static final String CODE = "INTERNAL_SERVER_ERROR";
    private static final HttpStatus STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    public InternalServerErrorException(String message, boolean visible) {
        super(message, STATUS, CODE, visible);
    }

    public InternalServerErrorException(String message, boolean visible, JsonNode data) {
        super(message, STATUS, CODE, visible, data);
    }

}
