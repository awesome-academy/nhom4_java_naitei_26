package nhom4.public_service_management_system.exception;

import java.time.LocalDateTime;
import java.util.List;

// Represents an error response for API requests
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        List<String> details
) {
    public static ApiError of(int status, String error, String message) {
        return new ApiError(LocalDateTime.now(), status, error, message, List.of());
    }

    public static ApiError of(int status, String error, String message, List<String> details) {
        return new ApiError(LocalDateTime.now(), status, error, message, details);
    }
}
