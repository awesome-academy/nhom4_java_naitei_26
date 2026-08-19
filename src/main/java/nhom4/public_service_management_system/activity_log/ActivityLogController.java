package nhom4.public_service_management_system.activity_log;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import nhom4.public_service_management_system.activity_log.dto.ActivityLogRequest;
import nhom4.public_service_management_system.activity_log.dto.ActivityLogResponse;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @PostMapping
    public ResponseEntity<ActivityLogResponse> create(@Valid @RequestBody ActivityLogRequest request) {
        ActivityLogResponse response = activityLogService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ActivityLogResponse>> getLogs(
            @RequestParam(required = false) Long userId,
            Pageable pageable) {
        if (userId != null) {
            return ResponseEntity.ok(activityLogService.findByUserId(userId, pageable));
        }
        return ResponseEntity.ok(activityLogService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityLogResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(activityLogService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        activityLogService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/old")
    public ResponseEntity<Void> deleteOldLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beforeDate) {
        activityLogService.deleteOldLogs(beforeDate);
        return ResponseEntity.noContent().build();
    }
}