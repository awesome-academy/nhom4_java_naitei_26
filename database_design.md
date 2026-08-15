# Database Description

## 1. users

Lưu tài khoản đăng nhập và phân quyền.

| Field | Type | Key | Description |
|---|---|---|---|
| `id` | BIGINT | PK | ID tài khoản |
| `email` | VARCHAR(255) | UNIQUE | Email đăng nhập |
| `password` | VARCHAR(255) | | Mật khẩu đã mã hóa |
| `role` | ENUM | | Vai trò |
| `status` | ENUM | | Trạng thái tài khoản |
| `email_notification_enabled` | BOOLEAN | | Bật/tắt email notification |

### role

```text
CITIZEN
STAFF
MANAGER
SUPER_ADMIN
```

### status

```text
ACTIVE
LOCKED
```

---

## 2. citizens

Lưu thông tin cá nhân của công dân.

| Field | Type | Key | Description |
|---|---|---|---|
| `id` | BIGINT | PK | ID công dân |
| `user_id` | BIGINT | FK, UNIQUE | Tài khoản tương ứng |
| `name` | VARCHAR(255) | | Họ tên |
| `date_of_birth` | DATE | | Ngày sinh |
| `gender` | ENUM | | Giới tính |
| `identity_number` | VARCHAR(20) | UNIQUE | Số CCCD/CMND |
| `address` | TEXT | | Địa chỉ |
| `phone` | VARCHAR(20) | | Số điện thoại |

---

## 3. staff

Lưu thông tin cán bộ.

| Field | Type | Key | Description |
|---|---|---|---|
| `id` | BIGINT | PK | ID cán bộ |
| `user_id` | BIGINT | FK, UNIQUE | Tài khoản tương ứng |
| `name` | VARCHAR(255) | | Họ tên |
| `phone` | VARCHAR(20) | | Số điện thoại |
| `address` | TEXT | | Địa chỉ |
| `department_id` | BIGINT | FK | Phòng ban mà cán bộ thuộc về |

---

## 4. departments

Lưu thông tin phòng ban/cơ quan.

| Field | Type | Key | Description |
|---|---|---|---|
| `id` | BIGINT | PK | ID phòng ban |
| `name` | VARCHAR(255) | | Tên phòng ban |
| `code` | VARCHAR(50) | UNIQUE | Mã phòng ban |
| `address` | TEXT | | Địa chỉ |
| `leader_staff_id` | BIGINT | FK | Cán bộ phụ trách |

---

## 5. services

Lưu thông tin các dịch vụ công.

| Field | Type | Key | Description |
|---|---|---|---|
| `id` | BIGINT | PK | ID dịch vụ |
| `name` | VARCHAR(255) | | Tên dịch vụ |
| `code` | VARCHAR(50) | UNIQUE | Mã dịch vụ |
| `description` | TEXT | | Mô tả |
| `category` | VARCHAR(100) | | Lĩnh vực |
| `processing_time` | INT | | Thời hạn xử lý |
| `fee` | DECIMAL(15,2) | | Lệ phí |
| `department_id` | BIGINT | FK | Phòng ban phụ trách |
| `assigned_staff_id` | BIGINT | FK | Cán bộ phụ trách dịch vụ |

---

## 6. service_required_documents

Lưu các tài liệu/biểu mẫu cần nộp của từng dịch vụ.

| Field | Type | Key | Description |
|---|---|---|---|
| `id` | BIGINT | PK | ID |
| `service_id` | BIGINT | FK | Dịch vụ |
| `document_name` | VARCHAR(255) | | Tên tài liệu |
| `required` | BOOLEAN | | Có bắt buộc hay không |

---

## 7. applications

Lưu hồ sơ dịch vụ công do công dân nộp.

| Field | Type | Key | Description |
|---|---|---|---|
| `id` | BIGINT | PK | ID hồ sơ |
| `application_code` | VARCHAR(50) | UNIQUE | Mã hồ sơ |
| `citizen_id` | BIGINT | FK | Công dân nộp hồ sơ |
| `service_id` | BIGINT | FK | Dịch vụ |
| `assigned_staff_id` | BIGINT | FK | Cán bộ hiện tại phụ trách |
| `status` | ENUM | | Trạng thái hồ sơ |
| `submitted_at` | DATETIME | | Thời gian nộp |
| `completed_at` | DATETIME | | Thời gian hoàn thành |
| `data` | JSON | | Thông tin nhập thêm của hồ sơ theo từng dịch vụ |
| `result_note` | TEXT | | Ghi chú kết quả |
| `rejection_reason` | TEXT | | Lý do từ chối |

### status

```text
RECEIVED
PROCESSING
APPROVED
REJECTED
```

---

## 8. application_documents

Lưu các tài liệu được upload liên quan đến hồ sơ.

| Field | Type | Key | Description |
|---|---|---|---|
| `id` | BIGINT | PK | ID tài liệu |
| `application_id` | BIGINT | FK | Hồ sơ |
| `document_type` | ENUM | | Loại tài liệu |
| `file_name` | VARCHAR(255) | | Tên file |
| `file_url` | VARCHAR(1000) | | Đường dẫn file |

### document_type

```text
SUBMISSION
ADDITIONAL
RESPONSE
```

---

## 9. application_histories

Lưu lịch sử thay đổi trạng thái của hồ sơ.

| Field | Type | Key | Description |
|---|---|---|---|
| `id` | BIGINT | PK | ID lịch sử |
| `application_id` | BIGINT | FK | Hồ sơ |
| `old_status` | ENUM | | Trạng thái trước |
| `new_status` | ENUM | | Trạng thái sau |
| `changed_by` | BIGINT | FK | Người thực hiện |
| `changed_at` | DATETIME | | Thời gian thay đổi |
| `note` | TEXT | | Ghi chú |

---

## 10. notifications

Lưu thông báo gửi đến người dùng.

| Field | Type | Key | Description |
|---|---|---|---|
| `id` | BIGINT | PK | ID thông báo |
| `user_id` | BIGINT | FK | Người nhận |
| `application_id` | BIGINT | FK | Hồ sơ liên quan |
| `message` | TEXT | | Nội dung |
| `is_read` | BOOLEAN | | Đã đọc hay chưa |

---

## 11. activity_logs

Lưu nhật ký hoạt động.

| Field | Type | Key | Description |
|---|---|---|---|
| `id` | BIGINT | PK | ID log |
| `created_at` | DATETIME | | Thời gian |
| `action` | VARCHAR(100) | | Hành động |
| `user_id` | BIGINT | FK | Người thực hiện |
| `description` | TEXT | | Mô tả |

---

# Relationships

## users — citizens

```text
users 1 ─── 0..1 citizens
```

## users — staff

```text
users 1 ─── 0..1 staff
```

## departments — staff

```text
departments 1 ─── N staff
```

## departments — services

```text
departments 1 ─── N services
```

## staff — services

```text
staff 1 ─── N services
```

## services — service_required_documents

```text
services 1 ─── N service_required_documents
```

## citizens — applications

```text
citizens 1 ─── N applications
```

## services — applications

```text
services 1 ─── N applications
```

## staff — applications

```text
staff 1 ─── N applications
```

## applications — application_documents

```text
applications 1 ─── N application_documents
```

## applications — application_histories

```text
applications 1 ─── N application_histories
```

## users — application_histories

```text
users 1 ─── N application_histories
```

## users — notifications

```text
users 1 ─── N notifications
```

## applications — notifications

```text
applications 1 ─── N notifications
```

## users — activity_logs

```text
users 1 ─── N activity_logs
```

---
