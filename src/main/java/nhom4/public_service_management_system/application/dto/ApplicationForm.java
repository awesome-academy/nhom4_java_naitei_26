package nhom4.public_service_management_system.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nhom4.public_service_management_system.application.ApplicationEntity;
import nhom4.public_service_management_system.enums.ApplicationStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationForm {

    @NotNull(message = "Vui lòng chọn công dân")
    private Long citizenId;

    @NotNull(message = "Vui lòng chọn dịch vụ công")
    private Long serviceId;

    private Long assignedStaffId;

    private ApplicationStatus status = ApplicationStatus.RECEIVED;

    private String note;

    private String resultNote;

    private String rejectionReason;

    public static ApplicationForm from(ApplicationResponse response) {
        if (response == null) {
            return new ApplicationForm();
        }
        ApplicationForm form = new ApplicationForm();
        form.setCitizenId(response.getCitizenId());
        form.setServiceId(response.getServiceId());
        form.setAssignedStaffId(response.getAssignedStaffId());
        form.setStatus(response.getStatus() != null ? response.getStatus() : ApplicationStatus.RECEIVED);
        form.setResultNote(response.getResultNote());
        form.setRejectionReason(response.getRejectionReason());

        if (response.getData() != null && response.getData().containsKey("note")) {
            Object noteVal = response.getData().get("note");
            form.setNote(noteVal != null ? noteVal.toString() : null);
        }
        return form;
    }

    public static ApplicationForm from(ApplicationEntity entity) {
        if (entity == null) {
            return new ApplicationForm();
        }
        ApplicationForm form = new ApplicationForm();
        form.setCitizenId(entity.getCitizenId());
        form.setServiceId(entity.getServiceId());
        form.setAssignedStaffId(entity.getAssignedStaffId());
        form.setStatus(entity.getStatus() != null ? entity.getStatus() : ApplicationStatus.RECEIVED);
        form.setResultNote(entity.getResultNote());
        form.setRejectionReason(entity.getRejectionReason());

        if (entity.getData() != null && entity.getData().containsKey("note")) {
            Object noteVal = entity.getData().get("note");
            form.setNote(noteVal != null ? noteVal.toString() : null);
        }
        return form;
    }

    public Map<String, Object> toDataMap() {
        Map<String, Object> map = new HashMap<>();
        if (note != null && !note.trim().isEmpty()) {
            map.put("note", note.trim());
        }
        return map;
    }
}
