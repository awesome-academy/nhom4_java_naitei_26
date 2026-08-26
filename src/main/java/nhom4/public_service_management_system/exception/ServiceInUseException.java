package nhom4.public_service_management_system.exception;

public class ServiceInUseException extends RuntimeException {

    public ServiceInUseException(String message) {
        super(message);
    }
}