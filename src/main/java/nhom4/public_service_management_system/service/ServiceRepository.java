package nhom4.public_service_management_system.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    Optional<ServiceEntity> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    Page<ServiceEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<ServiceEntity> findByCategory(String category, Pageable pageable);

    @Query("SELECT s FROM ServiceEntity s WHERE s.department.id = :departmentId")
    List<ServiceEntity> findByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT s FROM ServiceEntity s WHERE s.assignedStaff.id = :staffId")
    List<ServiceEntity> findByAssignedStaffIf(@Param("staffId") Long staffId);

    @Query("SELECT s FROM ServiceEntity s WHERE s.assignedStaff.id = :staffId")
    List<ServiceEntity> findByAssignedStaffId(@Param("staffId") Long staffId);
}
