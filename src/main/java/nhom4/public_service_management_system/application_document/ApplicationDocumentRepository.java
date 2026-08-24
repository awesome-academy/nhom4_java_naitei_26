package nhom4.public_service_management_system.application_document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationDocumentRepository extends JpaRepository<ApplicationDocumentEntity, Long> {
    List<ApplicationDocumentEntity> findByApplicationId(Long applicationId);
}
