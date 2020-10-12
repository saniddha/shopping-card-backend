package au.com.informedia.mailconnector.service.impl;

import au.com.informedia.mailconnector.dto.MailConfirmationRequest;
import au.com.informedia.mailconnector.dto.OutBoundRequest;
import au.com.informedia.mailconnector.dto.OutBoundResponse;
import au.com.informedia.mailconnector.service.EmailSendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MailSendStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailSendStrategy.class);

    private EmailSendService emailSendService;

    public MailSendStrategy() {
    }

    public MailSendStrategy(EmailSendService emailSendService) {
        this.emailSendService = emailSendService;
    }

    OutBoundResponse sendEmail(MailConfirmationRequest request){

        LOGGER.debug("Received the request for sending the mails => " + request);
        return emailSendService.sendEmail(request);
    }
}
