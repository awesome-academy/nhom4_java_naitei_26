package nhom4.public_service_management_system.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public class ApplicationRequest {
    @NotNull(message = "Service ID is required")
    private Long serviceId;

    @NotNull(message = "Application data is required")
    private Map<String, Object> data;

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}