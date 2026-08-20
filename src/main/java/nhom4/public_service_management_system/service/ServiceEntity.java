package nhom4.public_service_management_system.service;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import nhom4.public_service_management_system.department.DepartmentEntity;
import nhom4.public_service_management_system.staff.StaffEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "services", uniqueConstraints = {
        @UniqueConstraint(name = "uk_services_code", columnNames = "code")
})

public class ServiceEntity {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "processing_time")
    private Integer processingTime;

    @Column(name = "fee", precision = 15, scale = 2)
    private BigDecimal fee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private DepartmentEntity department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_staff_id")
    private StaffEntity assignedStaff;

    public ServiceEntity(String name, String code, String description, String category,
                         Integer processingTime, BigDecimal fee, DepartmentEntity department, StaffEntity assignedStaff) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.category = category;
        this.processingTime = processingTime;
        this.fee = fee;
        this.department = department;
        this.assignedStaff = assignedStaff;
    }
}