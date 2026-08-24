package nhom4.public_service_management_system.export;

import nhom4.public_service_management_system.application.ApplicationEntity;
import nhom4.public_service_management_system.application.ApplicationRepository;
import nhom4.public_service_management_system.citizen.CitizenEntity;
import nhom4.public_service_management_system.citizen.CitizenRepository;
import nhom4.public_service_management_system.department.DepartmentEntity;
import nhom4.public_service_management_system.department.DepartmentRepository;
import nhom4.public_service_management_system.service.ServiceEntity;
import nhom4.public_service_management_system.service.ServiceRepository;
import nhom4.public_service_management_system.staff.StaffEntity;
import nhom4.public_service_management_system.staff.StaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ExportCsvService {

    private final CitizenRepository citizenRepository;
    private final ApplicationRepository applicationRepository;
    private final ServiceRepository serviceRepository;
    private final DepartmentRepository departmentRepository;
    private final StaffRepository staffRepository;

    public ExportCsvService(
            CitizenRepository citizenRepository,
            ApplicationRepository applicationRepository,
            ServiceRepository serviceRepository,
            DepartmentRepository departmentRepository,
            StaffRepository staffRepository) {
        this.citizenRepository = citizenRepository;
        this.applicationRepository = applicationRepository;
        this.serviceRepository = serviceRepository;
        this.departmentRepository = departmentRepository;
        this.staffRepository = staffRepository;
    }

    // ─── Citizen ─────────────────────────────────────────────────────────────

    public String exportCitizensCsv() {
        List<CitizenEntity> citizens = citizenRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Họ và tên,Ngày sinh,Giới tính,Số CMND/CCCD,Địa chỉ,Số điện thoại,Ngày tạo,Số hồ sơ đã nộp\n");
        for (CitizenEntity c : citizens) {
            long totalApps = c.getApplications() != null ? c.getApplications().size() : 0;
            sb.append(csv(c.getId()))
              .append(",").append(csv(c.getName()))
              .append(",").append(csv(c.getDateOfBirth()))
              .append(",").append(csv(c.getGender() != null ? c.getGender().name() : ""))
              .append(",").append(csv(c.getIdentityNumber()))
              .append(",").append(csv(c.getAddress()))
              .append(",").append(csv(c.getPhone()))
              .append(",").append(csv(c.getCreatedAt()))
              .append(",").append(totalApps)
              .append("\n");
        }
        return sb.toString();
    }

    // ─── Application ─────────────────────────────────────────────────────────

    public String exportApplicationsCsv() {
        List<ApplicationEntity> apps = applicationRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Mã hồ sơ,ID Công dân,ID Dịch vụ,ID Cán bộ,Trạng thái,Ngày nộp,Ngày hoàn thành,Ghi chú kết quả,Lý do từ chối\n");
        for (ApplicationEntity a : apps) {
            sb.append(csv(a.getId()))
              .append(",").append(csv(a.getApplicationCode()))
              .append(",").append(csv(a.getCitizenId()))
              .append(",").append(csv(a.getServiceId()))
              .append(",").append(csv(a.getAssignedStaffId()))
              .append(",").append(csv(a.getStatus() != null ? a.getStatus().name() : ""))
              .append(",").append(csv(a.getSubmittedAt()))
              .append(",").append(csv(a.getCompletedAt()))
              .append(",").append(csv(a.getResultNote()))
              .append(",").append(csv(a.getRejectionReason()))
              .append("\n");
        }
        return sb.toString();
    }

    // ─── Service Type ─────────────────────────────────────────────────────────

    public String exportServiceTypesCsv() {
        List<ServiceEntity> services = serviceRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Tên dịch vụ,Mã dịch vụ,Mô tả,Danh mục,Thời gian xử lý (ngày),Phí (VND),ID Phòng ban,Tên phòng ban\n");
        for (ServiceEntity s : services) {
            String deptName = (s.getDepartment() != null) ? s.getDepartment().getName() : "";
            Long deptId = (s.getDepartment() != null) ? s.getDepartment().getId() : null;
            sb.append(csv(s.getId()))
              .append(",").append(csv(s.getName()))
              .append(",").append(csv(s.getCode()))
              .append(",").append(csv(s.getDescription()))
              .append(",").append(csv(s.getCategory()))
              .append(",").append(csv(s.getProcessingTime()))
              .append(",").append(csv(s.getFee()))
              .append(",").append(csv(deptId))
              .append(",").append(csv(deptName))
              .append("\n");
        }
        return sb.toString();
    }

    // ─── Department ───────────────────────────────────────────────────────────

    public String exportDepartmentsCsv() {
        List<DepartmentEntity> depts = departmentRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Tên phòng ban,Mã phòng ban,Địa chỉ,ID Trưởng phòng\n");
        for (DepartmentEntity d : depts) {
            Long leaderId = (d.getLeaderStaffId() != null) ? d.getLeaderStaffId().getId() : null;
            sb.append(csv(d.getId()))
              .append(",").append(csv(d.getName()))
              .append(",").append(csv(d.getCode()))
              .append(",").append(csv(d.getAddress()))
              .append(",").append(csv(leaderId))
              .append("\n");
        }
        return sb.toString();
    }

    // ─── Staff ────────────────────────────────────────────────────────────────

    public String exportStaffCsv() {
        List<StaffEntity> staffList = staffRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("ID,ID Người dùng,Họ và tên,Số điện thoại,Địa chỉ,ID Phòng ban\n");
        for (StaffEntity s : staffList) {
            sb.append(csv(s.getId()))
              .append(",").append(csv(s.getUserId()))
              .append(",").append(csv(s.getName()))
              .append(",").append(csv(s.getPhone()))
              .append(",").append(csv(s.getAddress()))
              .append(",").append(csv(s.getDepartmentId()))
              .append("\n");
        }
        return sb.toString();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Escape a value for CSV: wraps in double-quotes and escapes inner quotes.
     */
    private String csv(Object value) {
        if (value == null) return "";
        String str = value.toString();
        // Escape double-quotes and wrap field in quotes if it contains comma, newline or quote
        if (str.contains(",") || str.contains("\"") || str.contains("\n") || str.contains("\r")) {
            str = "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }
}
