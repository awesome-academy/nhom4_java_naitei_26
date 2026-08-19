package nhom4.public_service_management_system.citizen;

import nhom4.public_service_management_system.citizen.dto.CitizenRequest;
import nhom4.public_service_management_system.citizen.dto.CitizenResponse;
import org.springframework.stereotype.Component;

@Component
public class CitizenMapper {

    public CitizenEntity toEntity(CitizenRequest request) {
        if (request == null) {
            return null;
        }
        CitizenEntity entity = new CitizenEntity();
        applyRequest(entity, request);
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
        entity.setIdentityNumber(request.identityNumber());
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
                entity.getUpdatedAt()
        );
    }
}
