package nhom4.public_service_management_system.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ErrorResponse {
    private String message;
    private int status;
    private Map<String, String> errors;

    public ErrorResponse(String message, int status, Map<String, String> errors) {
        this.message = message;
        this.status = status;
        this.errors = errors;
    }

    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }
}


