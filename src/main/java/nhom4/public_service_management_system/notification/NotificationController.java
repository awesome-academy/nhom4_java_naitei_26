package nhom4.public_service_management_system.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import nhom4.public_service_management_system.auth.CustomUserDetails;
import nhom4.public_service_management_system.notification.dto.NotificationResponse;
import nhom4.public_service_management_system.notification.dto.NotificationSettingRequest;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController (NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {
        return ResponseEntity.ok(notificationService.getNotifications(userDetails.getId(), pageable));
    }

    @PatchMapping("/settings")
    public ResponseEntity<Void> toggleNotification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody NotificationSettingRequest request) {
        notificationService.toggleNotification(userDetails.getId(), request.enabled());
        return ResponseEntity.noContent().build();
    }
}





