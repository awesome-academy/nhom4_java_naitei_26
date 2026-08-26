package nhom4.public_service_management_system.application_document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import nhom4.public_service_management_system.application.ApplicationEntity;
import nhom4.public_service_management_system.enums.DocumentType;

@Entity
@Table(name = "application_documents")
@Getter
@Setter
public class ApplicationDocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "application_id")
    @JsonIgnore
    private ApplicationEntity application;

    private DocumentType documentType;
    private String fileName;
    private String fileUrl;
}
