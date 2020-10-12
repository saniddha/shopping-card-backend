package au.com.informedia.mailconnector.dto;

import lombok.Data;

@Data
public class OutBoundRequest {

    private int emailType;
    private int connectorType;

}
