package nhom4.public_service_management_system.citizen;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import nhom4.public_service_management_system.application.ApplicationMapper;
import nhom4.public_service_management_system.application.ApplicationEntity;
import nhom4.public_service_management_system.citizen.dto.CitizenUpdateRequest;
import nhom4.public_service_management_system.enums.Gender;
import org.junit.jupiter.api.Test;

class CitizenMapperTest {

    private final CitizenMapper citizenMapper = new CitizenMapper(new ApplicationMapper());

    @Test
    void applyUpdateRequest_shouldKeepIdentityNumberAndUserLink() {
        CitizenEntity citizen = new CitizenEntity();
        citizen.setIdentityNumber("123456789");
        citizen.setUserId(10L);

        CitizenUpdateRequest request = new CitizenUpdateRequest(
                99L,
                "Nguyen Van A",
                null,
                Gender.MALE,
                "Ha Noi",
                "0900000000"
        );

        citizenMapper.applyUpdateRequest(citizen, request);

        assertThat(citizen.getIdentityNumber()).isEqualTo("123456789");
        assertThat(citizen.getUserId()).isEqualTo(10L);
        assertThat(citizen.getName()).isEqualTo("Nguyen Van A");
    }

    @Test
    void toResponse_shouldIncludeSubmittedApplications() {
        CitizenEntity citizen = new CitizenEntity();
        citizen.setId(1L);
        ApplicationEntity application = new ApplicationEntity();
        application.setId(2L);
        application.setCitizen(citizen);
        citizen.setApplications(List.of(application));

        assertThat(citizenMapper.toResponse(citizen).applications())
                .singleElement()
                .extracting(response -> response.getId())
                .isEqualTo(2L);
    }
}