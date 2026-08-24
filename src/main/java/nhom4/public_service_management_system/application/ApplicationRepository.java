package nhom4.public_service_management_system.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {
    boolean existsByApplicationCode(String applicationCode);

    @Query("select application from ApplicationEntity application where application.citizen.id = :citizenId")
    java.util.List<ApplicationEntity> findByCitizenId(Long citizenId);
  
    List<ApplicationEntity> findByAssignedStaffId(Long assignedStaffId);

    List<ApplicationEntity> findAllByCitizenId(Long userId);

    Page<ApplicationEntity> findByAssignedStaffId(Long assignedStaffId, Pageable pageable);

    Optional<ApplicationEntity> findByIdAndAssignedStaffId(Long id, Long assignedStaffId);
}
