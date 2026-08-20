package nhom4.public_service_management_system.citizen;

import nhom4.public_service_management_system.citizen.dto.CitizenRequest;
import nhom4.public_service_management_system.citizen.dto.CitizenResponse;
import nhom4.public_service_management_system.citizen.dto.CitizenUpdateRequest;
import nhom4.public_service_management_system.exception.DuplicateResourceException;
import nhom4.public_service_management_system.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class CitizenService {

    private static final String RESOURCE_NAME = "Citizen";

    private final CitizenRepository citizenRepository;
    private final CitizenMapper citizenMapper;

    public CitizenService(CitizenRepository citizenRepository, CitizenMapper citizenMapper) {
        this.citizenRepository = citizenRepository;
        this.citizenMapper = citizenMapper;
    }

    public CitizenResponse create(CitizenRequest request) {
        validateUniqueIdentityNumber(request.identityNumber(), null);
        validateUniqueUserId(request.userId(), null);

        CitizenEntity entity = citizenMapper.toEntity(request);
        CitizenEntity saved = citizenRepository.save(entity);
        return citizenMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CitizenResponse getById(Long id) {
        CitizenEntity entity = findEntityOrThrow(id);
        return citizenMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public Page<CitizenResponse> getAll(Pageable pageable) {
        return citizenRepository.findAll(pageable).map(citizenMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<CitizenResponse> search(String name, Pageable pageable) {
        if (!StringUtils.hasText(name)) {
            return getAll(pageable);
        }
        return citizenRepository.findByNameContainingIgnoreCase(name, pageable).map(citizenMapper::toResponse);
    }

    public CitizenResponse update(Long id, CitizenUpdateRequest request) {
        CitizenEntity entity = findEntityOrThrow(id);

        citizenMapper.applyUpdateRequest(entity, request);
        CitizenEntity saved = citizenRepository.save(entity);
        return citizenMapper.toResponse(saved);
    }

    public CitizenResponse update(Long id, CitizenRequest request) {
        CitizenEntity entity = findEntityOrThrow(id);

        validateUniqueUserId(request.userId(), id);

        citizenMapper.applyRequest(entity, request);
        CitizenEntity saved = citizenRepository.save(entity);
        return citizenMapper.toResponse(saved);
    }

    public void delete(Long id) {
        if (!citizenRepository.existsById(id)) {
            throw new ResourceNotFoundException(RESOURCE_NAME + " not found with id: " + id);
        }
        citizenRepository.deleteById(id);
    }

    private CitizenEntity findEntityOrThrow(Long id) {
        return citizenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME + " not found with id: " + id));
    }

    private void validateUniqueIdentityNumber(String identityNumber, Long excludingId) {
        citizenRepository.findByIdentityNumber(identityNumber).ifPresent(existing -> {
            if (excludingId == null || !existing.getId().equals(excludingId)) {
                throw new DuplicateResourceException(
                        "Identity number '" + identityNumber + "' already belongs to another citizen");
            }
        });
    }

    private void validateUniqueUserId(Long userId, Long excludingId) {
        if (userId == null) {
            return;
        }
        citizenRepository.findByUserId(userId).ifPresent(existing -> {
            if (excludingId == null || !existing.getId().equals(excludingId)) {
                throw new DuplicateResourceException(
                        "User id '" + userId + "' is already linked to another citizen");
            }
        });
    }
}
