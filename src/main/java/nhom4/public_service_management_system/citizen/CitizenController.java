package nhom4.public_service_management_system.citizen;

import jakarta.validation.Valid;
import nhom4.public_service_management_system.citizen.dto.CitizenRequest;
import nhom4.public_service_management_system.citizen.dto.CitizenResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

// API CRUD for citizens
@RestController
@RequestMapping("/api/citizens")
public class CitizenController {

    private final CitizenService citizenService;

    public CitizenController(CitizenService citizenService) {
        this.citizenService = citizenService;
    }

    @PostMapping
    public ResponseEntity<CitizenResponse> create(@Valid @RequestBody CitizenRequest request) {
        CitizenResponse created = citizenService.create(request);
        return ResponseEntity.created(URI.create("/api/citizens/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitizenResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(citizenService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CitizenResponse>> getAll(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(citizenService.search(name, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CitizenResponse> update(@PathVariable Long id, @Valid @RequestBody CitizenRequest request) {
        return ResponseEntity.ok(citizenService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        citizenService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
