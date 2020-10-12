package au.com.informedia.mailconnector.service;

import au.com.informedia.mailconnector.dto.MailConfirmationRequest;
import au.com.informedia.mailconnector.dto.OutBoundRequest;
import au.com.informedia.mailconnector.dto.OutBoundResponse;

public interface EmailSendService {

    OutBoundResponse sendEmail(MailConfirmationRequest request);
}
