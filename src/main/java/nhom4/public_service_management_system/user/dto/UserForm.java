package nhom4.public_service_management_system.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import nhom4.public_service_management_system.enums.Gender;
import nhom4.public_service_management_system.enums.UserRole;
import nhom4.public_service_management_system.enums.UserStatus;

import java.time.LocalDate;

public class UserForm {

    @NotBlank(message = "Ten khong duoc de trong")
    @Size(max = 255, message = "Ten toi da 255 ky tu")
    private String name;

    @NotBlank(message = "Email khong duoc de trong")
    @Email(message = "Email khong dung dinh dang")
    private String email;

    private String password;

    @NotNull(message = "Vai tro khong duoc de trong")
    private UserRole role;

    private UserStatus status = UserStatus.ACTIVE;

    private Boolean emailNotificationEnabled = Boolean.TRUE;

    @Pattern(regexp = "^$|^[0-9+()\\-\\s]{8,20}$", message = "So dien thoai khong hop le")
    private String phone;

    @Size(max = 1000, message = "Dia chi toi da 1000 ky tu")
    private String address;

    @Past(message = "Ngay sinh phai la mot ngay trong qua khu")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private Gender gender;

    @Pattern(regexp = "^$|\\d{9}|\\d{12}", message = "So CCCD/CMND phai gom 9 hoac 12 chu so")
    private String identityNumber;

    public UserForm() {
    }

    public static UserForm from(UserProfileResponse response) {
        UserForm form = new UserForm();
        form.setName(response.name());
        form.setEmail(response.email());
        form.setRole(response.role());
        form.setStatus(response.status());
        form.setEmailNotificationEnabled(response.emailNotificationEnabled());
        form.setPhone(response.phone());
        form.setAddress(response.address());
        form.setDateOfBirth(response.dateOfBirth());
        form.setGender(response.gender());
        form.setIdentityNumber(response.identityNumber());
        return form;
    }

    public UserRequest toRequest() {
        return new UserRequest(email, password, role, status, emailNotificationEnabled);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Boolean getEmailNotificationEnabled() {
        return emailNotificationEnabled;
    }

    public void setEmailNotificationEnabled(Boolean emailNotificationEnabled) {
        this.emailNotificationEnabled = emailNotificationEnabled;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }
}
