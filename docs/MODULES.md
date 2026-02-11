# Backend Modules

## Module Overview

The Healthcare Platform backend is organized into 11 Maven modules following domain-driven design principles.

```
healthcare-platform/src/
├── pom.xml (parent)
├── healthcare-app (main application)
├── healthcare-common (shared utilities)
├── healthcare-patient
├── healthcare-appointment
├── healthcare-provider
├── healthcare-billing
├── healthcare-medical-record
├── healthcare-notification
├── healthcare-audit
├── healthcare-auth
└── healthcare-location
```

---

## healthcare-app

**Purpose:** Main Spring Boot application module

### Responsibilities
- Application entry point (`@SpringBootApplication`)
- Security configuration (JWT, CORS, CSRF)
- Database configuration (DataSource, JPA, Flyway)
- Cache configuration (Redis)
- Global exception handling
- Actuator endpoints
- WebMVC configuration

### Key Classes
- `HealthcareApplication.java` - Main application class
- `SecurityConfig.java` - Spring Security configuration
- `JwtAuthenticationFilter.java` - JWT token validation
- `GlobalExceptionHandler.java` - Centralized error handling
- `WebConfig.java` - CORS and MVC configuration

### Dependencies
- All other healthcare modules
- Spring Boot starters (web, security, data-jpa, redis)
- PostgreSQL driver
- Actuator

---

## healthcare-common

**Purpose:** Shared utilities and contracts used across all modules

### Components

#### DTOs
- `ApiResponse<T>` - Standard API response wrapper
- `PageResponse<T>` - Paginated response wrapper
- `ErrorResponse` - Error details structure

#### Exceptions
- `HealthcareException` - Base exception
- `ResourceNotFoundException` - Entity not found
- `ValidationException` - Business validation errors
- `UnauthorizedException` - Authentication failures
- `ForbiddenException` - Authorization failures

#### Utilities
- `DateUtils` - Date/time operations
- `StringUtils` - String manipulation
- `ValidationUtils` - Input validation helpers

#### Constants
- `HttpStatusConstants` - HTTP status codes
- `SecurityConstants` - Security-related constants
- `CacheConstants` - Cache key prefixes and TTLs

### No Dependencies
This module should not depend on any other healthcare module.

---

## healthcare-patient

**Purpose:** Patient management and demographics

### Domain Model

```java
@Entity
@Table(name = "patients")
public class Patient {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String email;
    private String phone;
    private String address;
    private PatientStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Features
- Patient registration
- Profile management
- Search and filtering
- Emergency contacts
- Insurance information
- Patient status tracking (ACTIVE, INACTIVE, DECEASED)

### REST Endpoints
```
GET    /api/patients              - List all patients (paginated)
GET    /api/patients/{id}         - Get patient by ID
POST   /api/patients              - Create new patient
PUT    /api/patients/{id}         - Update patient
DELETE /api/patients/{id}         - Soft delete patient
GET    /api/patients/search       - Search patients
```

### Key Classes
- `PatientController` - REST endpoints
- `PatientService` - Business logic
- `PatientRepository` - Data access
- `PatientMapper` - Entity ↔ DTO mapping
- `PatientRequest/Response` - DTOs

### Dependencies
- healthcare-common

---

## healthcare-appointment

**Purpose:** Appointment scheduling and management

### Domain Model

```java
@Entity
@Table(name = "appointments")
public class Appointment {
    private Long id;
    private Long patientId;
    private Long providerId;
    private Long locationId;
    private LocalDateTime scheduledDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentType type;
    private AppointmentStatus status;
    private String reason;
    private String notes;
}
```

### Features
- Appointment booking
- Schedule management
- Time slot availability checking
- Appointment status tracking (SCHEDULED, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW)
- Recurring appointments
- Appointment reminders (via notification module)

### REST Endpoints
```
GET    /api/appointments                   - List appointments
GET    /api/appointments/{id}              - Get appointment
POST   /api/appointments                   - Book appointment
PUT    /api/appointments/{id}              - Update appointment
DELETE /api/appointments/{id}              - Cancel appointment
GET    /api/appointments/availability      - Check availability
GET    /api/appointments/patient/{id}      - Patient's appointments
GET    /api/appointments/provider/{id}     - Provider's appointments
```

### Business Rules
- No overlapping appointments for same provider
- Minimum 30-minute slots
- Maximum 2 hours per appointment
- Cancellation must be 24 hours in advance
- Auto-confirm when booked by provider

### Dependencies
- healthcare-common
- healthcare-patient
- healthcare-provider
- healthcare-location
- healthcare-notification

---

## healthcare-provider

**Purpose:** Healthcare provider (doctor, nurse, etc.) management

### Domain Model

```java
@Entity
@Table(name = "providers")
public class Provider {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String licenseNumber;
    private String specialization;
    private ProviderStatus status;
    private LocalDateTime createdAt;
}
```

### Features
- Provider registration
- Specialization management
- Availability schedules
- Credentials tracking
- Provider search by specialization

### REST Endpoints
```
GET    /api/providers                - List all providers
GET    /api/providers/{id}           - Get provider
POST   /api/providers                - Create provider
PUT    /api/providers/{id}           - Update provider
DELETE /api/providers/{id}           - Deactivate provider
GET    /api/providers/specialization - Filter by specialization
```

### Dependencies
- healthcare-common

---

## healthcare-billing

**Purpose:** Billing, invoicing, and payment processing

### Domain Model

```java
@Entity
@Table(name = "invoices")
public class Invoice {
    private Long id;
    private Long patientId;
    private Long appointmentId;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal outstandingAmount;
    private InvoiceStatus status;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
}

@Entity
@Table(name = "invoice_items")
public class InvoiceItem {
    private Long id;
    private Long invoiceId;
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
```

### Features
- Invoice generation
- Payment tracking
- Insurance claims
- Billing statements
- Payment reminders
- Revenue reporting

### REST Endpoints
```
GET    /api/invoices              - List invoices
GET    /api/invoices/{id}         - Get invoice
POST   /api/invoices              - Create invoice
PUT    /api/invoices/{id}         - Update invoice
POST   /api/invoices/{id}/payment - Record payment
GET    /api/invoices/patient/{id} - Patient invoices
GET    /api/invoices/outstanding  - Outstanding invoices
```

### Business Rules
- Invoice total = sum of all items
- Outstanding = total - paid
- Status: DRAFT, SENT, PARTIAL_PAID, PAID, OVERDUE, CANCELLED
- Auto mark OVERDUE if past due date
- Send reminder 7 days before due date

### Dependencies
- healthcare-common
- healthcare-patient
- healthcare-appointment
- healthcare-notification

---

## healthcare-medical-record

**Purpose:** Electronic Medical Records (EMR)

### Domain Model

```java
@Entity
@Table(name = "medical_records")
public class MedicalRecord {
    private Long id;
    private Long patientId;
    private Long providerId;
    private Long appointmentId;
    private String chiefComplaint;
    private String diagnosis;
    private String treatment;
    private String prescription;
    private String notes;
    private LocalDateTime recordDate;
}
```

### Features
- Medical history tracking
- Diagnoses and treatments
- Prescriptions
- Lab results
- Document uploads (PDFs, images)
- Document versioning
- HIPAA-compliant access logging

### REST Endpoints
```
GET    /api/medical-records/patient/{id}  - Patient's records
GET    /api/medical-records/{id}          - Get record
POST   /api/medical-records               - Create record
PUT    /api/medical-records/{id}          - Update record
POST   /api/medical-records/{id}/document - Upload document
GET    /api/medical-records/{id}/document - Download document
```

### Security
- All access logged in audit module
- Role-based access control
- PHI encryption at rest
- No logging of PHI data

### Dependencies
- healthcare-common
- healthcare-patient
- healthcare-provider
- healthcare-appointment
- healthcare-audit

---

## healthcare-notification

**Purpose:** Multi-channel notification delivery

### Domain Model

```java
@Entity
@Table(name = "notifications")
public class Notification {
    private Long id;
    private Long recipientId;
    private NotificationType type;
    private NotificationChannel channel;
    private String subject;
    private String content;
    private NotificationStatus status;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
}
```

### Features
- Email notifications
- SMS notifications (future)
- In-app notifications
- Template management
- Delivery tracking
- Scheduled notifications
- Retry logic for failures

### Notification Types
- APPOINTMENT_REMINDER
- APPOINTMENT_CONFIRMATION
- APPOINTMENT_CANCELLED
- PAYMENT_DUE
- PAYMENT_RECEIVED
- LAB_RESULT_AVAILABLE
- PRESCRIPTION_READY

### REST Endpoints
```
GET    /api/notifications           - List notifications
GET    /api/notifications/{id}      - Get notification
POST   /api/notifications           - Send notification
GET    /api/notifications/user/{id} - User's notifications
PUT    /api/notifications/{id}/read - Mark as read
```

### Integration
- SMTP for email (configurable)
- Twilio for SMS (future)
- WebSocket for in-app (future)

### Dependencies
- healthcare-common

---

## healthcare-audit

**Purpose:** Audit logging and compliance

### Domain Model

```java
@Entity
@Table(name = "audit_events")
public class AuditEvent {
    private Long id;
    private Long userId;
    private String username;
    private String action;
    private String entity;
    private Long entityId;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
}
```

### Features
- User activity logging
- PHI access tracking
- HIPAA audit trails
- Compliance reporting
- Security event tracking
- Audit log search and filtering

### Audited Actions
- CREATE, UPDATE, DELETE operations
- LOGIN, LOGOUT events
- PHI_ACCESS (medical records, patient data)
- PERMISSION_CHANGE
- CONFIGURATION_CHANGE

### REST Endpoints
```
GET    /api/audit/events              - List audit events
GET    /api/audit/events/{id}         - Get event
GET    /api/audit/events/user/{id}    - User's activity
GET    /api/audit/events/entity/{type}/{id} - Entity history
GET    /api/audit/phi-access          - PHI access logs
```

### Retention Policy
- Standard logs: 1 year
- PHI access logs: 6 years (HIPAA requirement)
- Security events: 3 years

### Dependencies
- healthcare-common

---

## healthcare-auth

**Purpose:** Authentication and authorization

### Domain Model

```java
@Entity
@Table(name = "users")
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private UserStatus status;
    private LocalDateTime lastLogin;
    private Set<Role> roles;
}

@Entity
@Table(name = "roles")
public class Role {
    private Long id;
    private String name;
    private Set<Permission> permissions;
}
```

### Features
- User authentication (JWT)
- Role-based access control (RBAC)
- Password management
- Token refresh
- Session management
- User registration
- Password reset

### Roles
- `ROLE_ADMIN` - System administrator
- `ROLE_DOCTOR` - Healthcare provider
- `ROLE_NURSE` - Nursing staff
- `ROLE_RECEPTIONIST` - Front desk
- `ROLE_BILLING` - Billing department
- `ROLE_PATIENT` - Patient portal access

### REST Endpoints
```
POST   /api/auth/login          - User login
POST   /api/auth/logout         - User logout
POST   /api/auth/refresh        - Refresh token
POST   /api/auth/register       - User registration
POST   /api/auth/forgot-password - Password reset request
POST   /api/auth/reset-password  - Password reset
GET    /api/auth/me             - Current user info
```

### Security
- BCrypt password hashing
- JWT token expiration (1 hour)
- Refresh token rotation
- Failed login attempt tracking
- Account lockout after 5 failed attempts

### Dependencies
- healthcare-common

---

## healthcare-location

**Purpose:** Healthcare facility and location management

### Domain Model

```java
@Entity
@Table(name = "facilities")
public class Facility {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String phone;
    private FacilityType type;
}

@Entity
@Table(name = "departments")
public class Department {
    private Long id;
    private Long facilityId;
    private String name;
    private String code;
}

@Entity
@Table(name = "rooms")
public class Room {
    private Long id;
    private Long departmentId;
    private String roomNumber;
    private RoomType type;
    private RoomStatus status;
}
```

### Features
- Facility management
- Department organization
- Room tracking
- Resource allocation
- Operating hours
- Location hierarchy

### REST Endpoints
```
GET    /api/locations/facilities     - List facilities
GET    /api/locations/facilities/{id} - Get facility
GET    /api/locations/departments    - List departments
GET    /api/locations/rooms          - List rooms
GET    /api/locations/rooms/available - Available rooms
```

### Dependencies
- healthcare-common

---

## Module Dependencies Graph

```
healthcare-app
    ├── healthcare-common
    ├── healthcare-patient
    │   └── healthcare-common
    ├── healthcare-appointment
    │   ├── healthcare-common
    │   ├── healthcare-patient
    │   ├── healthcare-provider
    │   ├── healthcare-location
    │   └── healthcare-notification
    ├── healthcare-provider
    │   └── healthcare-common
    ├── healthcare-billing
    │   ├── healthcare-common
    │   ├── healthcare-patient
    │   ├── healthcare-appointment
    │   └── healthcare-notification
    ├── healthcare-medical-record
    │   ├── healthcare-common
    │   ├── healthcare-patient
    │   ├── healthcare-provider
    │   ├── healthcare-appointment
    │   └── healthcare-audit
    ├── healthcare-notification
    │   └── healthcare-common
    ├── healthcare-audit
    │   └── healthcare-common
    ├── healthcare-auth
    │   └── healthcare-common
    └── healthcare-location
        └── healthcare-common
```

## Best Practices

### Module Independence
- Modules should be loosely coupled
- Use events for cross-module communication
- Avoid circular dependencies
- Common utilities go in healthcare-common

### API Design
- Use DTOs for all API boundaries
- Never expose entities directly
- Consistent response structure
- Proper HTTP status codes

### Error Handling
- Use module-specific exceptions
- Extend HealthcareException from common
- Include meaningful error messages
- Log errors appropriately

### Testing
- Unit tests for service layer
- Integration tests for repositories
- Controller tests for REST endpoints
- Test coverage target: 80%

### Documentation
- Javadoc for public APIs
- README in each module
- API documentation (Swagger/OpenAPI)
- Architecture decision records (ADRs)
