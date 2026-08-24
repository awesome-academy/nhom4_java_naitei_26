package nhom4.public_service_management_system.application;

import nhom4.public_service_management_system.application.dto.ApplicationResponse;
import nhom4.public_service_management_system.application.dto.StaffActionResponse;
import nhom4.public_service_management_system.auth.CustomUserDetails;
import nhom4.public_service_management_system.staff.StaffEntity;
import nhom4.public_service_management_system.staff.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/staff/applications")
public class StaffApplicationRestController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private StaffRepository staffRepository;

    // --- Hàm hỗ trợ: Lấy Staff ID từ User đang đăng nhập ---
    private Long getStaffIdOrThrow(Long userId) {
        return staffRepository.findByUserId(userId)
                .map(StaffEntity::getId)
                .orElseThrow(() -> new RuntimeException("Lỗi truy cập: Tài khoản của bạn không thuộc hệ thống Cán bộ (Staff)!"));
    }

    @GetMapping
    public ResponseEntity<Page<ApplicationResponse>> getAssignedApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long staffId = getStaffIdOrThrow(userDetails.getId());
        Pageable pageable = PageRequest.of(page, size, Sort.by("submittedAt").descending());

        return ResponseEntity.ok(applicationService.getApplicationsForStaff(staffId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long staffId = getStaffIdOrThrow(userDetails.getId());
        return ResponseEntity.ok(applicationService.getApplicationDetailForStaff(id, staffId));
    }

    @PatchMapping("/{id}/process")
    public ResponseEntity<?> processApplication(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long userId = userDetails.getId();
            Long staffId = getStaffIdOrThrow(userId);

            ApplicationResponse updatedApp = applicationService.updateStatusToProcessing(id, staffId, userId);

            StaffActionResponse response = new StaffActionResponse(
                    "Tiếp nhận hồ sơ thành công. Đang xử lý.",
                    updatedApp.getApplicationCode(),
                    updatedApp.getStatus()
            );
            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }
}