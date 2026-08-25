package nhom4.public_service_management_system.application;

import jakarta.validation.Valid;
import nhom4.public_service_management_system.application.dto.ApplicationResponse;
import nhom4.public_service_management_system.application.dto.ManagerApproveRequest;
import nhom4.public_service_management_system.application.dto.ManagerRejectRequest;
import nhom4.public_service_management_system.application.dto.ManagerTransferRequest;
import nhom4.public_service_management_system.application_document.ApplicationDocumentEntity;
import nhom4.public_service_management_system.application_document.ApplicationDocumentService;
import nhom4.public_service_management_system.auth.CustomUserDetails;
import nhom4.public_service_management_system.enums.ApplicationStatus;
import nhom4.public_service_management_system.enums.DocumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * REST API cho Manager.
 * Base path: /api/manager/applications
 * Yêu cầu: ROLE_MANAGER
 */
@RestController
@RequestMapping("/api/manager/applications")
@PreAuthorize("hasRole('MANAGER')")
public class ManagerApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationDocumentService applicationDocumentService;

    /**
     * GET /api/manager/applications
     * Danh sách tất cả hồ sơ, có thể lọc theo status.
     *
     * @param status  (optional) lọc theo trạng thái: RECEIVED, PROCESSING, APPROVED, REJECTED
     * @param page    số trang (default 0)
     * @param size    kích thước trang (default 20)
     */
    @GetMapping
    public ResponseEntity<Page<ApplicationResponse>> listApplications(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ApplicationResponse> result = (status != null)
                ? applicationService.search(null, status, pageable)
                : applicationService.getAll(pageable);
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/manager/applications/{id}
     * Chi tiết một hồ sơ.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getById(id));
    }

    /**
     * PATCH /api/manager/applications/{id}/transfer
     * Chuyển hồ sơ sang cán bộ khác.
     *
     * Body: { "staffId": 123 }
     */
    @PatchMapping("/{id}/transfer")
    public ResponseEntity<ApplicationResponse> transfer(
            @PathVariable Long id,
            @Valid @RequestBody ManagerTransferRequest request,
            @AuthenticationPrincipal CustomUserDetails manager) {

        ApplicationResponse response = applicationService.transferToStaff(
                id, request.staffId(), manager.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/manager/applications/{id}/approve
     * Duyệt hồ sơ: PROCESSING → APPROVED, kèm ghi chú kết quả.
     *
     * Body: { "resultNote": "Đủ điều kiện, đã xử lý xong." }
     */
    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApplicationResponse> approve(
            @PathVariable Long id,
            @RequestBody(required = false) ManagerApproveRequest request,
            @AuthenticationPrincipal CustomUserDetails manager) {

        String resultNote = (request != null) ? request.resultNote() : null;
        ApplicationResponse response = applicationService.approveApplication(
                id, resultNote, manager.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/manager/applications/{id}/approve/documents
     * Upload tài liệu phản hồi sau khi duyệt hồ sơ.
     * Document type mặc định: RESPONSE
     *
     * Form-data: file (MultipartFile), documentType (optional, default RESPONSE)
     */
    @PostMapping(value = "/{id}/approve/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApplicationDocumentEntity> uploadResponseDocument(
            @PathVariable Long id,
            @RequestParam MultipartFile file,
            @RequestParam(defaultValue = "RESPONSE") DocumentType documentType) throws IOException {

        ApplicationDocumentEntity doc = applicationDocumentService.create(id, documentType, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(doc);
    }

    /**
     * PATCH /api/manager/applications/{id}/reject
     * Từ chối hồ sơ: PROCESSING → REJECTED, kèm lý do từ chối.
     *
     * Body: { "rejectionReason": "Hồ sơ không hợp lệ vì..." }
     */
    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApplicationResponse> reject(
            @PathVariable Long id,
            @Valid @RequestBody ManagerRejectRequest request,
            @AuthenticationPrincipal CustomUserDetails manager) {

        ApplicationResponse response = applicationService.rejectApplication(
                id, request.rejectionReason(), manager.getId());
        return ResponseEntity.ok(response);
    }

    // ─── Exception Handlers ───────────────────────────────────────────────────

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("status", 409, "message", ex.getMessage(), "errors", "null"));
    }
}
