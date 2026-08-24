package nhom4.public_service_management_system.application;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {
    boolean existsByApplicationCode(String applicationCode);

    @Query("select application from ApplicationEntity application where application.citizen.id = :citizenId")
    List<ApplicationEntity> findByCitizenId(@Param("citizenId") Long citizenId);
  
    List<ApplicationEntity> findByAssignedStaffId(Long assignedStaffId);

    @Query("select application from ApplicationEntity application where application.citizen.user.id = :userId")
    List<ApplicationEntity> findByCitizenUserId(@Param("userId") Long userId);
}

