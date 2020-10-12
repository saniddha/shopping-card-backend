package au.com.informedia.mailconnector.controller;


import au.com.informedia.mailconnector.dto.MailConfirmationRequest;
import au.com.informedia.mailconnector.dto.OutBoundRequest;
import au.com.informedia.mailconnector.dto.OutBoundResponse;
import au.com.informedia.mailconnector.service.EmailService;
import au.com.informedia.mailconnector.service.impl.EmailServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/outbound")
public class EmailControler {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailControler.class);

    private EmailService emailService;

    public EmailControler(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    @PostMapping(value = "/request/confirm", consumes = "application/json")
    public ResponseEntity<OutBoundResponse> saveBooking(@RequestBody MailConfirmationRequest request) {
        LOGGER.debug("Email Send Request recieved for => " + request);
        OutBoundResponse response = emailService.sendConfirmationEmail(request);
        LOGGER.info("Received response after senting the mail => " + request);
        return  ResponseEntity.ok(response);
    }
}
