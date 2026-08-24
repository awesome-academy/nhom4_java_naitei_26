package nhom4.public_service_management_system.department;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import nhom4.public_service_management_system.staff.StaffEntity;

@Entity
@Table(name = "departments", uniqueConstraints = {
        @UniqueConstraint(name = "uk_departments_code", columnNames = "code")
})

public class DepartmentEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "name", nullable = false, length = 255)
        private String name;

        @Column(name = "code", nullable = false, unique = true, length = 50)
        private String code;

        @Column(name = "address", columnDefinition = "TEXT")
        private String address;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "leader_staff_id")
        private StaffEntity leaderStaffId;

        public DepartmentEntity() {
        }

        public DepartmentEntity(String name, String code, String address, StaffEntity leaderStaffId) {
            this.name = name;
            this.code = code;
            this.address = address;
            this.leaderStaffId = leaderStaffId;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public StaffEntity getLeaderStaffId() {
            return leaderStaffId;
        }

        public void setLeaderStaffId(StaffEntity leaderStaffId) {
            this.leaderStaffId = leaderStaffId;
        }
}

