package nhom4.public_service_management_system.import_csv;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/import")
public class ImportPageController {
    private final CsvImportService csvImportService;

    public ImportPageController(CsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }

    @GetMapping
    public String index() {
        return "import/index";
    }

    @PostMapping("/citizens")
    public String importCitizens(@RequestParam MultipartFile file, RedirectAttributes redirectAttributes) {
        return importFile(() -> csvImportService.importCitizens(file), "citizen", redirectAttributes);
    }

    @PostMapping("/services")
    public String importServices(@RequestParam MultipartFile file, RedirectAttributes redirectAttributes) {
        return importFile(() -> csvImportService.importServices(file), "service", redirectAttributes);
    }

    @PostMapping("/departments")
    public String importDepartments(@RequestParam MultipartFile file, RedirectAttributes redirectAttributes) {
        return importFile(() -> csvImportService.importDepartments(file), "department", redirectAttributes);
    }

    @PostMapping("/staff")
    public String importStaff(@RequestParam MultipartFile file, RedirectAttributes redirectAttributes) {
        return importFile(() -> csvImportService.importStaff(file), "staff", redirectAttributes);
    }

    private String importFile(ImportAction action, String resourceName, RedirectAttributes redirectAttributes) {
        try {
            int imported = action.importFile();
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Imported " + imported + " " + resourceName + " records successfully.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/import";
    }

    @FunctionalInterface
    private interface ImportAction {
        int importFile();
    }
}
