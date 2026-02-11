# API Documentation

## Base URL

- **Development:** `http://localhost:8080`
- **Production:** `https://api.yourdomain.com`

## Authentication

All API endpoints (except auth endpoints) require JWT authentication.

### Headers

```http
Authorization: Bearer <jwt-token>
Content-Type: application/json
```

### Getting a Token

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "type": "Bearer",
  "expiresIn": 3600000,
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@healthcare.com",
    "roles": ["ROLE_ADMIN"]
  }
}
```

---

## Standard Response Format

### Success Response

```json
{
  "status": "success",
  "data": { ... },
  "message": "Operation completed successfully"
}
```

### Error Response

```json
{
  "status": "error",
  "error": {
    "code": "RESOURCE_NOT_FOUND",
    "message": "Patient not found with id: 123",
    "timestamp": "2026-02-11T10:30:00Z",
    "path": "/api/patients/123"
  }
}
```

### Paginated Response

```json
{
  "status": "success",
  "data": {
    "content": [ ... ],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5,
    "last": false,
    "first": true
  }
}
```

---

## Patients API

### List Patients

```http
GET /api/patients?page=0&size=20&sort=lastName,asc&status=ACTIVE
```

**Query Parameters:**
- `page` (optional): Page number, default 0
- `size` (optional): Page size, default 20
- `sort` (optional): Sort field and direction
- `status` (optional): Filter by status (ACTIVE, INACTIVE, DECEASED)

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "dateOfBirth": "1980-05-15",
      "gender": "MALE",
      "email": "john.doe@email.com",
      "phone": "+1234567890",
      "status": "ACTIVE",
      "createdAt": "2026-01-15T10:00:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 50,
  "totalPages": 3
}
```

### Get Patient by ID

```http
GET /api/patients/{id}
```

**Response:**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1980-05-15",
  "gender": "MALE",
  "email": "john.doe@email.com",
  "phone": "+1234567890",
  "address": "123 Main St, New York, NY 10001",
  "status": "ACTIVE",
  "emergencyContact": {
    "name": "Jane Doe",
    "relationship": "Spouse",
    "phone": "+1234567891"
  },
  "insurance": {
    "provider": "Blue Cross",
    "policyNumber": "BC123456789",
    "groupNumber": "GRP001"
  }
}
```

### Create Patient

```http
POST /api/patients
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1980-05-15",
  "gender": "MALE",
  "email": "john.doe@email.com",
  "phone": "+1234567890",
  "address": "123 Main St, New York, NY 10001"
}
```

**Validation Rules:**
- `firstName`: Required, 2-100 characters
- `lastName`: Required, 2-100 characters
- `dateOfBirth`: Required, must be in past
- `gender`: Required, enum (MALE, FEMALE, OTHER)
- `email`: Required, valid email format, unique
- `phone`: Required, valid phone format

**Response:** 201 Created
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  ...
}
```

### Update Patient

```http
PUT /api/patients/{id}
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe Updated",
  "phone": "+1987654321"
}
```

**Response:** 200 OK

### Delete Patient

```http
DELETE /api/patients/{id}
```

**Response:** 204 No Content

### Search Patients

```http
GET /api/patients/search?query=John&page=0&size=20
```

**Query Parameters:**
- `query`: Search term (searches first name, last name, email)
- `page`, `size`: Pagination

---

## Appointments API

### List Appointments

```http
GET /api/appointments?page=0&size=20&status=SCHEDULED&from=2026-02-11&to=2026-02-18
```

**Query Parameters:**
- `status`: Filter by status (SCHEDULED, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW)
- `from`: Start date (ISO 8601)
- `to`: End date (ISO 8601)
- `patientId`: Filter by patient
- `providerId`: Filter by provider

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "patient": {
        "id": 1,
        "name": "John Doe"
      },
      "provider": {
        "id": 2,
        "name": "Dr. Smith",
        "specialization": "Cardiology"
      },
      "scheduledDate": "2026-02-15",
      "startTime": "10:00:00",
      "endTime": "10:30:00",
      "type": "CONSULTATION",
      "status": "SCHEDULED",
      "reason": "Annual checkup"
    }
  ]
}
```

### Book Appointment

```http
POST /api/appointments
Content-Type: application/json

{
  "patientId": 1,
  "providerId": 2,
  "locationId": 1,
  "scheduledDate": "2026-02-15",
  "startTime": "10:00:00",
  "endTime": "10:30:00",
  "type": "CONSULTATION",
  "reason": "Annual checkup"
}
```

**Validation:**
- Times must be in future
- Duration: 30 min - 2 hours
- No overlapping appointments for provider
- Provider must be available

**Response:** 201 Created

### Check Availability

```http
GET /api/appointments/availability?providerId=2&date=2026-02-15
```

**Response:**
```json
{
  "date": "2026-02-15",
  "providerId": 2,
  "availableSlots": [
    {
      "startTime": "09:00:00",
      "endTime": "09:30:00"
    },
    {
      "startTime": "10:00:00",
      "endTime": "10:30:00"
    }
  ]
}
```

### Cancel Appointment

```http
DELETE /api/appointments/{id}
Content-Type: application/json

{
  "reason": "Patient requested cancellation"
}
```

**Business Rules:**
- Must cancel at least 24 hours in advance
- Sends notification to patient and provider

---

## Providers API

### List Providers

```http
GET /api/providers?specialization=CARDIOLOGY&status=ACTIVE
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "firstName": "Jane",
      "lastName": "Smith",
      "email": "dr.smith@healthcare.com",
      "phone": "+1234567890",
      "specialization": "CARDIOLOGY",
      "licenseNumber": "LIC123456",
      "status": "ACTIVE"
    }
  ]
}
```

### Get Provider Availability

```http
GET /api/providers/{id}/availability?from=2026-02-11&to=2026-02-18
```

---

## Billing API

### List Invoices

```http
GET /api/invoices?status=OUTSTANDING&patientId=1
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "patient": {
        "id": 1,
        "name": "John Doe"
      },
      "totalAmount": 250.00,
      "paidAmount": 100.00,
      "outstandingAmount": 150.00,
      "status": "PARTIAL_PAID",
      "dueDate": "2026-03-01",
      "items": [
        {
          "description": "Consultation Fee",
          "quantity": 1,
          "unitPrice": 150.00,
          "totalPrice": 150.00
        },
        {
          "description": "Lab Test",
          "quantity": 1,
          "unitPrice": 100.00,
          "totalPrice": 100.00
        }
      ]
    }
  ]
}
```

### Create Invoice

```http
POST /api/invoices
Content-Type: application/json

{
  "patientId": 1,
  "appointmentId": 5,
  "items": [
    {
      "description": "Consultation Fee",
      "quantity": 1,
      "unitPrice": 150.00
    }
  ],
  "dueDate": "2026-03-01"
}
```

### Record Payment

```http
POST /api/invoices/{id}/payment
Content-Type: application/json

{
  "amount": 100.00,
  "method": "CREDIT_CARD",
  "transactionId": "TXN123456",
  "notes": "Partial payment"
}
```

---

## Medical Records API

### Get Patient Medical Records

```http
GET /api/medical-records/patient/{patientId}
```

**Authorization:** Requires `ROLE_DOCTOR` or owner

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "patientId": 1,
      "provider": {
        "id": 2,
        "name": "Dr. Smith"
      },
      "appointmentId": 5,
      "chiefComplaint": "Chest pain",
      "diagnosis": "Angina pectoris",
      "treatment": "Prescribed medication",
      "prescription": "Aspirin 81mg daily",
      "recordDate": "2026-02-10T14:30:00Z"
    }
  ]
}
```

### Create Medical Record

```http
POST /api/medical-records
Content-Type: application/json

{
  "patientId": 1,
  "providerId": 2,
  "appointmentId": 5,
  "chiefComplaint": "Chest pain",
  "diagnosis": "Angina pectoris",
  "treatment": "Prescribed medication",
  "prescription": "Aspirin 81mg daily"
}
```

**Authorization:** Requires `ROLE_DOCTOR`

### Upload Document

```http
POST /api/medical-records/{id}/document
Content-Type: multipart/form-data

file: <binary data>
documentType: LAB_RESULT
```

**Supported Formats:** PDF, JPEG, PNG
**Max Size:** 10MB

---

## Notifications API

### Get User Notifications

```http
GET /api/notifications/user/{userId}?read=false
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "type": "APPOINTMENT_REMINDER",
      "subject": "Appointment Reminder",
      "content": "You have an appointment tomorrow at 10:00 AM",
      "status": "SENT",
      "read": false,
      "sentAt": "2026-02-10T18:00:00Z"
    }
  ]
}
```

### Mark as Read

```http
PUT /api/notifications/{id}/read
```

---

## Audit API

### Get Audit Events

```http
GET /api/audit/events?action=PHI_ACCESS&from=2026-02-01&to=2026-02-11
```

**Authorization:** Requires `ROLE_ADMIN`

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "userId": 2,
      "username": "dr.smith",
      "action": "PHI_ACCESS",
      "entity": "MedicalRecord",
      "entityId": 5,
      "ipAddress": "192.168.1.100",
      "timestamp": "2026-02-10T14:30:00Z",
      "metadata": {
        "patientId": 1,
        "reason": "Treatment review"
      }
    }
  ]
}
```

---

## Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `RESOURCE_NOT_FOUND` | 404 | Resource not found |
| `VALIDATION_ERROR` | 400 | Input validation failed |
| `UNAUTHORIZED` | 401 | Authentication required |
| `FORBIDDEN` | 403 | Insufficient permissions |
| `DUPLICATE_RESOURCE` | 409 | Resource already exists |
| `BUSINESS_RULE_VIOLATION` | 422 | Business rule violated |
| `INTERNAL_SERVER_ERROR` | 500 | Server error |

---

## Rate Limiting

- **Authenticated:** 1000 requests per hour
- **Unauthenticated:** 100 requests per hour

Headers:
```http
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1644595200
```

---

## Versioning

API version is included in the URL:

```
/api/v1/patients
/api/v2/patients  (future)
```

Current version: `v1` (default)

---

## Health Check

```http
GET /actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "redis": { "status": "UP" },
    "diskSpace": { "status": "UP" }
  }
}
```

---

## Interactive Documentation

Swagger UI: `http://localhost:8080/swagger-ui.html`

OpenAPI Spec: `http://localhost:8080/v3/api-docs`
