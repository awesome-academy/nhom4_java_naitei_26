package nhom4.public_service_management_system.staff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import nhom4.public_service_management_system.exception.DuplicateResourceException;
import nhom4.public_service_management_system.exception.ResourceNotFoundException;
import nhom4.public_service_management_system.staff.dto.StaffRequest;
import nhom4.public_service_management_system.staff.dto.StaffResponse;

@ExtendWith(MockitoExtension.class)
class StaffServiceTest {
    @Mock
    private StaffRepository staffRepository;

    private StaffMapper staffMapper;
    private StaffService staffService;

    private StaffEntity staffEntity;
    private StaffRequest staffRequest;

    @BeforeEach
    void setUp() {
        staffMapper = new StaffMapper();
        staffService = new StaffService(staffRepository, staffMapper);

        staffEntity = new StaffEntity();
        staffEntity.setId(1L);
        staffEntity.setUserId(10L);
        staffEntity.setName("Nguyen Van A");
        staffEntity.setPhone("0900000000");
        staffEntity.setAddress("Ha Noi");
        staffEntity.setDepartmentId(2L);

        staffRequest = new StaffRequest(
                10L,
                "Nguyen Van A",
                "0900000000",
                "Ha Noi",
                2L
        );
    }

    @Test
    void create_shouldReturnStaffResponse_whenUserIdNotExists() {
        when(staffRepository.existsByUserId(staffRequest.userId())).thenReturn(false);
        when(staffRepository.save(any(StaffEntity.class))).thenReturn(staffEntity);

        StaffResponse response = staffService.create(staffRequest);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.userId()).isEqualTo(10L);
        assertThat(response.name()).isEqualTo("Nguyen Van A");
        verify(staffRepository).save(any(StaffEntity.class));
    }

    @Test
    void create_shouldThrowException_whenUserIdAlreadyExists() {
        when(staffRepository.existsByUserId(staffRequest.userId())).thenReturn(true);

        assertThatThrownBy(() -> staffService.create(staffRequest))
                .isInstanceOf(DuplicateResourceException.class);

        verify(staffRepository, never()).save(any(StaffEntity.class));
    }

    @Test
    void getById_shouldReturnStaffResponse_whenStaffExists() {
        when(staffRepository.findById(1L)).thenReturn(Optional.of(staffEntity));

        StaffResponse response = staffService.getById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.userId()).isEqualTo(10L);
    }

    @Test
    void getById_shouldThrowException_whenStaffNotFound() {
        when(staffRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> staffService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldUpdateStaff_whenStaffExists() {
        StaffRequest updateRequest = new StaffRequest(
                11L,
                "Tran Thi B",
                "0911111111",
                "Da Nang",
                3L
        );

        when(staffRepository.findById(1L)).thenReturn(Optional.of(staffEntity));
        when(staffRepository.existsByUserIdAndIdNot(updateRequest.userId(), 1L)).thenReturn(false);
        when(staffRepository.save(any(StaffEntity.class))).thenReturn(staffEntity);

        StaffResponse response = staffService.update(1L, updateRequest);

        assertThat(response).isNotNull();
        assertThat(staffEntity.getUserId()).isEqualTo(11L);
        assertThat(staffEntity.getName()).isEqualTo("Tran Thi B");
        verify(staffRepository).save(staffEntity);
    }

    @Test
    void update_shouldThrowException_whenStaffNotFound() {
        when(staffRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> staffService.update(99L, staffRequest))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(staffRepository, never()).save(any(StaffEntity.class));
    }

    @Test
    void update_shouldThrowException_whenUserIdAlreadyUsedByAnotherStaff() {
        when(staffRepository.findById(1L)).thenReturn(Optional.of(staffEntity));
        when(staffRepository.existsByUserIdAndIdNot(staffRequest.userId(), 1L)).thenReturn(true);

        assertThatThrownBy(() -> staffService.update(1L, staffRequest))
                .isInstanceOf(DuplicateResourceException.class);

        verify(staffRepository, never()).save(any(StaffEntity.class));
    }

    @Test
    void delete_shouldRemoveStaff_whenStaffExists() {
        when(staffRepository.existsById(1L)).thenReturn(true);

        staffService.delete(1L);

        verify(staffRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrowException_whenStaffNotFound() {
        when(staffRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> staffService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(staffRepository, never()).deleteById(anyLong());
    }
}
