package nhom4.public_service_management_system.application;

import java.util.List;

import nhom4.public_service_management_system.application.dto.ApplicationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {
    boolean existsByApplicationCode(String applicationCode);

    @Query("select application from ApplicationEntity application where application.citizen.id = :citizenId")
    java.util.List<ApplicationEntity> findByCitizenId(Long citizenId);
  
    List<ApplicationEntity> findByAssignedStaffId(Long assignedStaffId);

    List<ApplicationEntity> findAllByCitizenId(Long userId);
}
