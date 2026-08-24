package nhom4.public_service_management_system.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @Query("SELECT n FROM NotificationEntity n WHERE n.user.id = :userId")
    Page<NotificationEntity> findByUserId(@Param("userId") Long userId, Pageable pageable);

}