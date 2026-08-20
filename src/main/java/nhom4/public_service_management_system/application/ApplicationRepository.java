package nhom4.public_service_management_system.application;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {
    boolean existsByApplicationCode(String applicationCode);

    List<ApplicationEntity> findByAssignedStaffId(Long assignedStaffId);
}
