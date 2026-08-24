package nhom4.public_service_management_system.application;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import nhom4.public_service_management_system.application.dto.ApplicationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nhom4.public_service_management_system.enums.ApplicationStatus;

import nhom4.public_service_management_system.enums.ApplicationStatus;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {
    boolean existsByApplicationCode(String applicationCode);

    @Query("select application from ApplicationEntity application where application.citizen.id = :citizenId")
    List<ApplicationEntity> findByCitizenId(@Param("citizenId") Long citizenId);

    java.util.List<ApplicationEntity> findByCitizenId(Long citizenId);
  
    List<ApplicationEntity> findByAssignedStaffId(Long assignedStaffId);

    @Query("select application from ApplicationEntity application where application.citizen.user.id = :userId")
    List<ApplicationEntity> findByCitizenUserId(@Param("userId") Long userId);

    @Query("SELECT a FROM ApplicationEntity a WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(a.applicationCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR (a.citizen IS NOT NULL AND (LOWER(a.citizen.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR a.citizen.identityNumber LIKE CONCAT('%', :keyword, '%')))) " +
           "AND (:status IS NULL OR a.status = :status)")
    Page<ApplicationEntity> searchApplications(
            @Param("keyword") String keyword,
            @Param("status") ApplicationStatus status,
            Pageable pageable);

    Page<ApplicationEntity> findByStatus(ApplicationStatus status, Pageable pageable);

    long countByStatus(ApplicationStatus status);
}
    List<ApplicationEntity> findAllByCitizenId(Long userId);
}

