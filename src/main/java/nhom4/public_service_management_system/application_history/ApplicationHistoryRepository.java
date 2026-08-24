package nhom4.public_service_management_system.application_history;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationHistoryRepository extends JpaRepository<ApplicationHistoryEntity, Long> {
    List<ApplicationHistoryEntity> findByApplicationIdOrderByChangedAtDesc(Long applicationId);
}
