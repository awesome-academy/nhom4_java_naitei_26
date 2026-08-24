package nhom4.public_service_management_system.department;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> {

    Optional<DepartmentEntity> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    Page<DepartmentEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT d FROM DepartmentEntity d WHERE d.leaderStaffId.id = :leaderStaffId")
    Optional<DepartmentEntity> findByLeaderStaffId(@Param("leaderStaffId") Long leaderStaffId);
}