package nhom4.public_service_management_system.application;

import nhom4.public_service_management_system.application.dto.ApplicationRequest;
import nhom4.public_service_management_system.application.dto.ApplicationResponse;
import nhom4.public_service_management_system.citizen.CitizenEntity;
import nhom4.public_service_management_system.citizen.CitizenRepository;
import nhom4.public_service_management_system.enums.ApplicationStatus;
import nhom4.public_service_management_system.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CitizenRepository citizenRepository;

    @Autowired
    private ApplicationMapper applicationMapper;

    public ApplicationResponse create(ApplicationRequest request, Long userId) {
        ApplicationEntity entity = applicationMapper.toEntity(request);
        
        CitizenEntity citizen = citizenRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin công dân cho tài khoản này"));
        entity.setCitizen(citizen);
        entity.setStatus(ApplicationStatus.RECEIVED);
        entity.setAssignedStaffId(null);
        entity.setCompletedAt(null);
        entity.setResultNote(null);
        entity.setRejectionReason(null);
        
        ApplicationEntity savedEntity = applicationRepository.save(entity);
        return applicationMapper.toResponse(savedEntity);
    }

    public List<ApplicationResponse> getApplication(Long userId) {
        List<ApplicationEntity> applicationEntityList = applicationRepository.findByCitizenUserId(userId);
        return applicationEntityList.stream()
                .map(applicationMapper::toResponse)
                .toList();
    }

    public ApplicationResponse getById(Long id) {
        ApplicationEntity entity = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ với id: " + id));
        return applicationMapper.toResponse(entity);
    }
}
