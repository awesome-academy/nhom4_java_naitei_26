package nhom4.public_service_management_system.activity_log;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLogEntity, Long> {

    Page<ActivityLogEntity> findByUserId(Long userId, Pageable pageable);

    void deleteByCreatedAtBefore(LocalDateTime date);
}