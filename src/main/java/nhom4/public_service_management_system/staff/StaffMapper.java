package nhom4.public_service_management_system.staff;

import org.springframework.stereotype.Component;

import nhom4.public_service_management_system.staff.dto.StaffRequest;
import nhom4.public_service_management_system.staff.dto.StaffResponse;

@Component
public class StaffMapper {
    public StaffEntity toEntity(StaffRequest request) {
        StaffEntity staff = new StaffEntity();
        updateEntity(staff, request);
        return staff;
    }

    public StaffResponse toResponse(StaffEntity staff) {
        String departmentName = staff.getDepartment() != null ? staff.getDepartment().getName() : null;
        return new StaffResponse(
                staff.getId(),
                staff.getUserId(),
                staff.getName(),
                staff.getPhone(),
                staff.getAddress(),
                staff.getDepartmentId(),
                departmentName
        );
    }

    public void updateEntity(StaffEntity staff, StaffRequest request) {
        staff.setUserId(request.userId());
        staff.setName(request.name());
        staff.setPhone(request.phone());
        staff.setAddress(request.address());
        staff.setDepartmentId(request.departmentId());
    }
}
