package au.com.informedia.mailconnector.service.impl;

import au.com.informedia.mailconnector.config.ConfigConst;
import au.com.informedia.mailconnector.dto.MailConfirmationRequest;
import au.com.informedia.mailconnector.dto.OutBoundRequest;
import au.com.informedia.mailconnector.dto.OutBoundResponse;
import au.com.informedia.mailconnector.persistance.repository.nosql.BookingRepository;
import au.com.informedia.mailconnector.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final ConfigConst configConst;

    public EmailServiceImpl(BookingRepository bookingRepository, ConfigConst configConst) {
        this.bookingRepository = bookingRepository;
        this.configConst = configConst;
    }

    @Override
    public OutBoundResponse sendConfirmationEmail(MailConfirmationRequest request) {

        LOGGER.debug("Received mail confirmation request => " + request);
        MailSendStrategy mailSendStrategy = new MailSendStrategy(new SesConfirmationMailSender(configConst));
        return mailSendStrategy.sendEmail(request);
    }
}
