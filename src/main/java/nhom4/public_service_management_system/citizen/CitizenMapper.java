package nhom4.public_service_management_system.citizen;

import nhom4.public_service_management_system.application.ApplicationMapper;
import nhom4.public_service_management_system.citizen.dto.CitizenRequest;
import nhom4.public_service_management_system.citizen.dto.CitizenResponse;
import nhom4.public_service_management_system.citizen.dto.CitizenUpdateRequest;
import org.springframework.stereotype.Component;

@Component
public class CitizenMapper {

    private final ApplicationMapper applicationMapper;

    public CitizenMapper(ApplicationMapper applicationMapper) {
        this.applicationMapper = applicationMapper;
    }

    public CitizenEntity toEntity(CitizenRequest request) {
        if (request == null) {
            return null;
        }
        CitizenEntity entity = new CitizenEntity();
        applyRequest(entity, request);
        entity.setIdentityNumber(request.identityNumber());
        return entity;
    }

    public void applyRequest(CitizenEntity entity, CitizenRequest request) {
        if (entity == null || request == null) {
            return;
        }
        entity.setUserId(request.userId());
        entity.setName(request.name());
        entity.setDateOfBirth(request.dateOfBirth());
        entity.setGender(request.gender());
        entity.setAddress(request.address());
        entity.setPhone(request.phone());
    }

    public void applyUpdateRequest(CitizenEntity entity, CitizenUpdateRequest request) {
        if (entity == null || request == null) {
            return;
        }
        entity.setName(request.name());
        entity.setDateOfBirth(request.dateOfBirth());
        entity.setGender(request.gender());
        entity.setAddress(request.address());
        entity.setPhone(request.phone());
    }

    public CitizenResponse toResponse(CitizenEntity entity) {
        if (entity == null) {
            return null;
        }
        return new CitizenResponse(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                entity.getDateOfBirth(),
                entity.getGender(),
                entity.getIdentityNumber(),
                entity.getAddress(),
                entity.getPhone(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getApplications().stream()
                        .map(applicationMapper::toResponse)
                        .toList()
        );
    }
}
