package nhom4.public_service_management_system.application;

import nhom4.public_service_management_system.application.dto.ApplicationRequest;
import nhom4.public_service_management_system.application.dto.ApplicationResponse;
import nhom4.public_service_management_system.enums.ApplicationStatus;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationMapper applicationMapper;

    public ApplicationResponse create(ApplicationRequest request, Long userId) {
        ApplicationEntity entity = applicationMapper.toEntity(request);
        entity.setCitizenId(userId);
        entity.setStatus(ApplicationStatus.RECEIVED);
        entity.setAssignedStaffId(null);
        entity.setCompletedAt(null);
        entity.setResultNote(null);
        entity.setRejectionReason(null);
        
        ApplicationEntity savedEntity = applicationRepository.save(entity);
        return applicationMapper.toResponse(savedEntity);
    }

    public List<ApplicationResponse> getApplication(Long userId) {
        List<ApplicationResponse> applicationResponseList = new ArrayList<>();
        List<ApplicationEntity> applicationEntityList = applicationRepository.findAllByCitizenId(userId);
        for (ApplicationEntity applicationEntity : applicationEntityList) {
            applicationMapper.toResponse(applicationEntity);
        }
        return applicationResponseList;
    }

    public ApplicationResponse getById(Long id) {
        return applicationMapper.toResponse(applicationRepository.findById(id).get());
    }
}
