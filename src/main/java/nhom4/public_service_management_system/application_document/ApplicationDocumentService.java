package nhom4.public_service_management_system.application_document;

import lombok.RequiredArgsConstructor;
import nhom4.public_service_management_system.application.ApplicationEntity;
import nhom4.public_service_management_system.application.ApplicationRepository;
import nhom4.public_service_management_system.enums.DocumentType;
import nhom4.public_service_management_system.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationDocumentService {
    private final ApplicationRepository applicationRepository;
    private final ApplicationDocumentRepository applicationDocumentRepository;
    private final String uploadDir = "uploads/applications/";

    public ApplicationDocumentEntity create(Long applicationId, DocumentType documentType, MultipartFile file)
        throws IOException {
        ApplicationEntity application = applicationRepository
                .findById(applicationId)
                .orElseThrow(()->new ResourceNotFoundException("application not found"));
        Path applicationDir = Paths.get(uploadDir + applicationId);
        Files.createDirectories(applicationDir);
        String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = applicationDir.resolve(storedFileName);
        // Lưu file thật
        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );
        // Lưu thông tin file vào DB
        ApplicationDocumentEntity document =
                new ApplicationDocumentEntity();
        document.setApplication(application);
        document.setDocumentType(documentType);
        document.setFileName(file.getOriginalFilename());
        document.setFileUrl(filePath.toString());
        return applicationDocumentRepository.save(document);
    }
}
