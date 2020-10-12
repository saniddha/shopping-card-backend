package au.com.informedia.mailconnector.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class ExceptionResponse {

    @JsonProperty("status_code")
    private String code;

    @JsonProperty("description")
    private String description;

    @JsonProperty("is_visible")
    private int isVisible=0;

    @JsonProperty("data")
    private Object data;

    private ExceptionResponse(int code, String description){
        super();
        this.code = String.valueOf(code);
        this.description = description;
    }

    private ExceptionResponse(String code, String description){
        super();
        this.code = code;
        this.description = description;
    }

    public ExceptionResponse(int code, String description, int isVisible){
        this(code, description);
        this.isVisible = isVisible;
    }

    public ExceptionResponse(String code, String description, int isVisible){
        this(code, description);
        this.isVisible = isVisible;
    }

    public ExceptionResponse(String code, String description, int isVisible, Object data){
        this(code, description);
        this.isVisible = isVisible;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }
}