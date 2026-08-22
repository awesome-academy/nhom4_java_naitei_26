package nhom4.public_service_management_system.export;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Controller
@RequestMapping("/admin/export")
public class ExportPageController {

    private final ExportCsvService exportCsvService;

    public ExportPageController(ExportCsvService exportCsvService) {
        this.exportCsvService = exportCsvService;
    }

    /**
     * Trang chính: hiển thị UI chọn loại dữ liệu cần export.
     */
    @GetMapping
    public String exportPage() {
        return "export/index";
    }

    /**
     * Endpoint download CSV.
     * @param type một trong: citizen | application | service | department | staff
     */
    @GetMapping("/csv")
    public ResponseEntity<byte[]> downloadCsv(@RequestParam String type) {
        String csvContent;
        String filename;
        String today = LocalDate.now().toString();

        switch (type) {
            case "citizen" -> {
                csvContent = exportCsvService.exportCitizensCsv();
                filename = "citizens_" + today + ".csv";
            }
            case "application" -> {
                csvContent = exportCsvService.exportApplicationsCsv();
                filename = "applications_" + today + ".csv";
            }
            case "service" -> {
                csvContent = exportCsvService.exportServiceTypesCsv();
                filename = "service_types_" + today + ".csv";
            }
            case "department" -> {
                csvContent = exportCsvService.exportDepartmentsCsv();
                filename = "departments_" + today + ".csv";
            }
            case "staff" -> {
                csvContent = exportCsvService.exportStaffCsv();
                filename = "staff_" + today + ".csv";
            }
            default -> {
                return ResponseEntity.badRequest().build();
            }
        }

        // Add BOM (U+FEFF) for UTF-8 so Excel can detect encoding correctly
        byte[] bomBytes = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] contentBytes = csvContent.getBytes(StandardCharsets.UTF_8);
        byte[] responseBytes = new byte[bomBytes.length + contentBytes.length];
        System.arraycopy(bomBytes, 0, responseBytes, 0, bomBytes.length);
        System.arraycopy(contentBytes, 0, responseBytes, bomBytes.length, contentBytes.length);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(responseBytes);
    }
}
