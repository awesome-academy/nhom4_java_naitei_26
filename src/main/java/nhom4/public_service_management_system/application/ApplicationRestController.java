package nhom4.public_service_management_system.application;

import jakarta.validation.Valid;
import nhom4.public_service_management_system.application.dto.ApplicationRequest;
import nhom4.public_service_management_system.application.dto.ApplicationResponse;
import nhom4.public_service_management_system.application_document.ApplicationDocumentEntity;
import nhom4.public_service_management_system.application_document.ApplicationDocumentService;
import nhom4.public_service_management_system.auth.CustomUserDetails;
import nhom4.public_service_management_system.enums.DocumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationRestController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationDocumentService applicationDocumentService;

    @PostMapping
    public ResponseEntity<ApplicationResponse> create(@Valid @RequestBody ApplicationRequest request,
                                                      @AuthenticationPrincipal CustomUserDetails user) {
        Long userId = user.getId();
        ApplicationResponse response = applicationService.create(request, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/{applicationId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDocument(
            @PathVariable Long applicationId,
            @RequestParam DocumentType documentType,
            @RequestParam MultipartFile file
            ) throws IOException {
        ApplicationDocumentEntity documentEntity = applicationDocumentService.create(
                applicationId, documentType, file
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(documentEntity);
    }

    @GetMapping()
    public ResponseEntity<List<ApplicationResponse>> myApplication(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        return ResponseEntity.ok().body(applicationService.getApplication(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok().body(applicationService.getById(id));
    }
}
