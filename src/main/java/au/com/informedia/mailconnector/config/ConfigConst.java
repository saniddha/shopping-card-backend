package au.com.informedia.mailconnector.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.ApplicationScope;

@Configuration
@ApplicationScope
@Data
public class ConfigConst {

    @Value(value = "${cors.url}")
    private String corsUrl;

    @Value(value = "${mailgun.username}")
    private String mailGunUserName;
    @Value(value = "${mailgun.key}")
    private String mailGunKey;
    @Value(value = "${mailgun.url}")
    private String mailGunUrl;
    @Value(value ="${mailgun.url.append}")
    private String mailGunUrlAppend;
    @Value(value ="${mailgun.confirmation.template}")
    private String templateName;
    @Value(value ="${mailgun.confirmation.subject}")
    private String subject;
    @Value(value ="${mailgun.systemmail}")
    private String systemMail;
    @Value(value ="${mailgun.domainname}")
    private String domainName;
    @Value(value ="${booking.cancel.url}")
    private String bookingCancelUrl;
    @Value(value ="${dealer.directions.url}")
    private String dealerLocation;
    @Value(value ="${mail.notofiction}")
    private String mailNotofication;
    @Value(value ="${aws.ses.auth.accesskey}")
    private String sesAccesskey;
    @Value(value ="${aws.ses.auth.secretkey}")
    private String sesSecretkey;
    @Value(value ="${ses.email.confirm.subject}")
    private String sesConfirmSubject;
    @Value(value ="${ses.email.cancel.subject}")
    private String sesCancelSubject;
    @Value(value ="${ses.email.edit.subject}")
    private String sesEditSubject;

    public static int priceDecimal = 2;
    public static int locationDecimal = 6;

}
