package au.com.informedia.mailconnector.exception.handler;

import au.com.informedia.mailconnector.dto.common.ExceptionResponse;
import au.com.informedia.mailconnector.exception.RootException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({RootException.class})
    public final ResponseEntity<ExceptionResponse> throwGlobalException(RootException e) {

        ExceptionResponse er = new ExceptionResponse(e.getStatusCode(), e.getMessage(), 1, e.getData());
        return new ResponseEntity<>(er, e.getStatus());

    }

    @ExceptionHandler({ConstraintViolationException.class})
    public final ResponseEntity<ExceptionResponse> onConstraintValidationException(ConstraintViolationException e) {

        ExceptionResponse er = new ExceptionResponse("BAD_REQUEST", e.getMessage(), 1, null);
        return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {

        ExceptionResponse er = new ExceptionResponse("BAD_REQUEST", "Your request cannot be fulfilled", 1, null);
        return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
    }
}
