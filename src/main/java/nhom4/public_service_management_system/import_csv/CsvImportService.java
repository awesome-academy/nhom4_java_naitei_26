package nhom4.public_service_management_system.import_csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import nhom4.public_service_management_system.citizen.CitizenEntity;
import nhom4.public_service_management_system.citizen.CitizenRepository;
import nhom4.public_service_management_system.department.DepartmentEntity;
import nhom4.public_service_management_system.department.DepartmentRepository;
import nhom4.public_service_management_system.enums.Gender;
import nhom4.public_service_management_system.enums.UserRole;
import nhom4.public_service_management_system.enums.UserStatus;
import nhom4.public_service_management_system.service.ServiceEntity;
import nhom4.public_service_management_system.service.ServiceRepository;
import nhom4.public_service_management_system.staff.StaffEntity;
import nhom4.public_service_management_system.staff.StaffRepository;
import nhom4.public_service_management_system.user.UserEntity;
import nhom4.public_service_management_system.user.UserRepository;

@Service
@Transactional
public class CsvImportService {
    private final CitizenRepository citizenRepository;
    private final DepartmentRepository departmentRepository;
    private final ServiceRepository serviceRepository;
    private final StaffRepository staffRepository;
    private final UserRepository userRepository;

    public CsvImportService(
            CitizenRepository citizenRepository,
            DepartmentRepository departmentRepository,
            ServiceRepository serviceRepository,
            StaffRepository staffRepository,
            UserRepository userRepository) {
        this.citizenRepository = citizenRepository;
        this.departmentRepository = departmentRepository;
        this.serviceRepository = serviceRepository;
        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
    }

    public int importCitizens(MultipartFile file) {
        return importRows(file, row -> {
            String email = value(row, 0);
            String identityNumber = value(row, 5);
            String phone = value(row, 7);

            if (userRepository.existsByEmail(email)
                    || citizenRepository.existsByIdentityNumber(identityNumber)
                    || citizenRepository.findByPhone(phone).isPresent()) {
                return false;
            }

            UserEntity user = new UserEntity();
            user.setEmail(email);
            user.setPassword(value(row, 1));
            user.setRole(UserRole.ROLE_CITIZEN);
            user.setStatus(UserStatus.ACTIVE);
            user.setEmailNotificationEnabled(true);
            UserEntity savedUser = userRepository.save(user);

            CitizenEntity citizen = new CitizenEntity();
            citizen.setUser(savedUser);
            citizen.setName(value(row, 2));
            citizen.setDateOfBirth(parseDate(value(row, 3)));
            citizen.setGender(parseEnum(Gender.class, value(row, 4)));
            citizen.setIdentityNumber(identityNumber);
            citizen.setAddress(value(row, 6));
            citizen.setPhone(phone);
            citizenRepository.save(citizen);
            return true;
        });
    }

    public int importServices(MultipartFile file) {
        return importRows(file, row -> {
            String code = value(row, 1);
            if (serviceRepository.existsByCode(code)) {
                return false;
            }

            ServiceEntity service = new ServiceEntity();
            service.setName(value(row, 0));
            service.setCode(code);
            service.setDescription(value(row, 2));
            service.setCategory(value(row, 3));
            service.setProcessingTime(parseInteger(value(row, 4)));
            service.setFee(parseBigDecimal(value(row, 5)));
            service.setDepartment(findDepartment(value(row, 6)));
            service.setAssignedStaff(findStaff(value(row, 7)));
            serviceRepository.save(service);
            return true;
        });
    }

    public int importDepartments(MultipartFile file) {
        return importRows(file, row -> {
            String code = value(row, 1);
            if (departmentRepository.existsByCode(code)) {
                return false;
            }

            DepartmentEntity department = new DepartmentEntity();
            department.setName(value(row, 0));
            department.setCode(code);
            department.setAddress(value(row, 2));
            department.setLeaderStaffId(findStaff(value(row, 3)));
            departmentRepository.save(department);
            return true;
        });
    }

    public int importStaff(MultipartFile file) {
        return importRows(file, row -> {
            Long userId = parseLong(value(row, 0));
            String phone = value(row, 2);
            if (staffRepository.existsByUserId(userId) || staffRepository.findByPhone(phone).isPresent()) {
                return false;
            }

            StaffEntity staff = new StaffEntity();
            staff.setUserId(userId);
            staff.setName(value(row, 1));
            staff.setPhone(phone);
            staff.setAddress(value(row, 3));
            staff.setDepartmentId(parseLong(value(row, 4)));
            staffRepository.save(staff);
            return true;
        });
    }

    private int importRows(MultipartFile file, RowImporter importer) {
        validateCsvFile(file);
        int imported = 0;
        List<String[]> rows = readRows(file);
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (!isEmptyRow(row) && importer.importRow(row)) {
                imported++;
            }
        }
        return imported;
    }

    private List<String[]> readRows(MultipartFile file) {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                rows.add(parseCsvLine(line));
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Cannot read CSV file", ex);
        }
        return rows;
    }

    private void validateCsvFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("CSV file is required");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("Only .csv files are supported");
        }
    }

    private String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (character == ',' && !inQuotes) {
                values.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }
        values.add(current.toString().trim());
        return values.toArray(String[]::new);
    }

    private boolean isEmptyRow(String[] row) {
        for (String value : row) {
            if (value != null && !value.isBlank()) {
                return false;
            }
        }
        return true;
    }

    private String value(String[] row, int index) {
        return index >= row.length || row[index] == null ? "" : row[index].trim();
    }

    private LocalDate parseDate(String value) {
        return value.isBlank() ? null : LocalDate.parse(value);
    }

    private Long parseLong(String value) {
        return value.isBlank() ? null : Long.valueOf(value);
    }

    private Integer parseInteger(String value) {
        return value.isBlank() ? null : Integer.valueOf(value);
    }

    private BigDecimal parseBigDecimal(String value) {
        return value.isBlank() ? null : new BigDecimal(value);
    }

    private <T extends Enum<T>> T parseEnum(Class<T> enumType, String value) {
        return value.isBlank() ? null : Enum.valueOf(enumType, value);
    }

    private DepartmentEntity findDepartment(String id) {
        Long departmentId = parseLong(id);
        return departmentId == null
                ? null
                : departmentRepository.findById(departmentId)
                        .orElseThrow(() -> new IllegalArgumentException("Department not found with id: " + id));
    }

    private StaffEntity findStaff(String id) {
        Long staffId = parseLong(id);
        return staffId == null
                ? null
                : staffRepository.findById(staffId)
                        .orElseThrow(() -> new IllegalArgumentException("Staff not found with id: " + id));
    }

    @FunctionalInterface
    private interface RowImporter {
        boolean importRow(String[] row);
    }
}
