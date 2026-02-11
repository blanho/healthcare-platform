# System Architecture

## Overview

Healthcare Platform follows a modern microservices-inspired modular monolith architecture with clear separation of concerns.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         Frontend (React)                         │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │
│  │Dashboard │  │Patients  │  │Appts     │  │Billing   │        │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘        │
└─────────────────────────────────────────────────────────────────┘
                              │
                         HTTPS/REST
                              │
┌─────────────────────────────────────────────────────────────────┐
│                    API Gateway / Load Balancer                   │
└─────────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────────┐
│                    Spring Boot Backend                           │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              healthcare-app (Main Module)                │   │
│  │  - Spring Boot Application Entry Point                   │   │
│  │  - Configuration & Security                              │   │
│  │  - Actuator Endpoints                                    │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                  │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐       │
│  │   Patient     │  │  Appointment  │  │    Billing    │       │
│  │   Module      │  │    Module     │  │    Module     │       │
│  └───────────────┘  └───────────────┘  └───────────────┘       │
│                                                                  │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐       │
│  │   Provider    │  │    Medical    │  │     Auth      │       │
│  │   Module      │  │    Record     │  │    Module     │       │
│  └───────────────┘  └───────────────┘  └───────────────┘       │
│                                                                  │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐       │
│  │  Notification │  │     Audit     │  │   Location    │       │
│  │    Module     │  │    Module     │  │    Module     │       │
│  └───────────────┘  └───────────────┘  └───────────────┘       │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │            healthcare-common (Shared Module)             │   │
│  │  - DTOs, Exceptions, Utilities                           │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              │               │               │
         ┌────▼─────┐   ┌────▼─────┐   ┌────▼─────┐
         │PostgreSQL│   │  Redis   │   │  Spaces  │
         │    16    │   │   Cache  │   │   (S3)   │
         └──────────┘   └──────────┘   └──────────┘
```

## Module Breakdown

### Core Modules

#### healthcare-app
- **Purpose:** Main application entry point
- **Responsibilities:**
  - Spring Boot application configuration
  - Security configuration (JWT, CORS, CSRF)
  - Database configuration (HikariCP, JPA)
  - Actuator endpoints for monitoring
  - Global exception handling
  - Cross-cutting concerns

#### healthcare-common
- **Purpose:** Shared utilities and contracts
- **Components:**
  - Common DTOs and response models
  - Custom exceptions hierarchy
  - Utility classes (date, string, validation)
  - Shared constants and enums
  - Base entity classes

### Domain Modules

#### healthcare-patient
- **Domain:** Patient management
- **Features:**
  - Patient registration and profiles
  - Demographics management
  - Contact information
  - Emergency contacts
  - Insurance information
  - Patient search and filtering

#### healthcare-appointment
- **Domain:** Appointment scheduling
- **Features:**
  - Appointment booking and management
  - Schedule management
  - Time slot availability
  - Appointment status tracking
  - Recurring appointments
  - Appointment reminders

#### healthcare-provider
- **Domain:** Healthcare provider management
- **Features:**
  - Provider profiles
  - Specializations
  - Availability schedules
  - Provider-patient assignments
  - Credentials management

#### healthcare-billing
- **Domain:** Billing and invoicing
- **Features:**
  - Invoice generation
  - Payment processing
  - Insurance claims
  - Billing statements
  - Payment tracking
  - Revenue reporting

#### healthcare-medical-record
- **Domain:** Electronic Medical Records (EMR)
- **Features:**
  - Patient medical history
  - Diagnoses and treatments
  - Prescriptions
  - Lab results
  - Medical documents
  - Document versioning

#### healthcare-notification
- **Domain:** Notification services
- **Features:**
  - Email notifications
  - SMS notifications (future)
  - In-app notifications
  - Notification templates
  - Notification preferences
  - Delivery tracking

#### healthcare-audit
- **Domain:** Audit logging
- **Features:**
  - User activity logging
  - PHI access tracking
  - Compliance reporting
  - Audit trail query
  - HIPAA audit logs

#### healthcare-auth
- **Domain:** Authentication & Authorization
- **Features:**
  - User authentication (JWT)
  - Role-based access control (RBAC)
  - Token management
  - Password management
  - Session management
  - OAuth2 integration (future)

#### healthcare-location
- **Domain:** Location management
- **Features:**
  - Healthcare facility management
  - Departments
  - Rooms and resources
  - Location hierarchy
  - Operating hours

## Data Flow

### Typical Request Flow

```
User Browser
    │
    │ 1. HTTP Request (JWT in Header)
    ▼
React Frontend
    │
    │ 2. API Call via Axios
    ▼
Backend API Gateway
    │
    │ 3. JWT Validation (Spring Security)
    ▼
REST Controller
    │
    │ 4. DTO Validation (@Valid)
    ▼
Service Layer
    │
    │ 5. Business Logic
    ├──────────────────┐
    │                  │
    ▼                  ▼
Repository         Redis Cache
    │                  │
    │ 6. Query         │ Cache Lookup
    ▼                  │
PostgreSQL             │
    │                  │
    │ 7. Results       │
    └──────────────────┘
    │
    │ 8. DTO Mapping (MapStruct)
    ▼
REST Controller
    │
    │ 9. JSON Response
    ▼
React Frontend
    │
    │ 10. UI Update
    ▼
User Browser
```

## Database Architecture

### Schema Organization

```
healthcare_db
│
├── patients
│   ├── patients
│   ├── emergency_contacts
│   └── insurance_info
│
├── appointments
│   ├── appointments
│   ├── schedules
│   └── time_slots
│
├── providers
│   ├── providers
│   ├── specializations
│   └── provider_schedules
│
├── billing
│   ├── invoices
│   ├── invoice_items
│   ├── payments
│   └── insurance_claims
│
├── medical_records
│   ├── medical_records
│   ├── diagnoses
│   ├── prescriptions
│   └── documents
│
├── auth
│   ├── users
│   ├── roles
│   ├── permissions
│   └── user_roles
│
├── notifications
│   ├── notifications
│   └── notification_templates
│
├── audit
│   └── audit_events
│
└── locations
    ├── facilities
    ├── departments
    └── rooms
```

## Caching Strategy

### Redis Cache Layers

1. **Session Cache**
   - User sessions
   - JWT token blacklist
   - TTL: 1 hour

2. **Entity Cache**
   - Patient lookup by ID
   - Provider lookup by ID
   - TTL: 15 minutes

3. **Query Cache**
   - Appointment availability
   - Dashboard statistics
   - TTL: 5 minutes

4. **Reference Data Cache**
   - Specializations
   - Locations
   - TTL: 24 hours

## Security Architecture

### Authentication Flow

```
1. User Login (username/password)
    ↓
2. AuthController validates credentials
    ↓
3. Generate JWT token (signed with secret)
    ↓
4. Return token to client
    ↓
5. Client stores token (localStorage)
    ↓
6. Include token in Authorization header
    ↓
7. JwtAuthenticationFilter validates token
    ↓
8. Extract user details and roles
    ↓
9. Set SecurityContext
    ↓
10. Proceed to controller method
```

### Authorization Layers

1. **Method-level:** `@PreAuthorize("hasRole('DOCTOR')")`
2. **Class-level:** `@Secured("ROLE_ADMIN")`
3. **URL-level:** SecurityFilterChain configuration
4. **Data-level:** Custom authorization logic

## Deployment Architecture

### Production Setup (DigitalOcean)

```
┌─────────────────────────────────────────────┐
│         DigitalOcean Load Balancer          │
│              (SSL Termination)              │
└─────────────────────────────────────────────┘
                    │
        ┌───────────┴───────────┐
        │                       │
┌───────▼────────┐    ┌────────▼────────┐
│   Frontend     │    │   Backend       │
│   Container    │    │   Container     │
│   (Nginx)      │    │   (Java 21)     │
└────────────────┘    └─────────────────┘
                            │
              ┌─────────────┼─────────────┐
              │             │             │
    ┌─────────▼──┐   ┌─────▼─────┐  ┌───▼─────┐
    │ PostgreSQL │   │   Redis   │  │ Spaces  │
    │  (Managed) │   │ (Managed) │  │  (S3)   │
    └────────────┘   └───────────┘  └─────────┘
```

## Scalability Considerations

### Horizontal Scaling
- Stateless backend services
- Load balancer distribution
- Shared session in Redis
- Connection pooling (HikariCP)

### Vertical Scaling
- Database optimization (indexes, queries)
- JVM tuning (heap size, GC)
- Connection pool sizing
- Cache hit rate optimization

### Database Scaling
- Read replicas for reporting
- Connection pooling
- Query optimization
- Partitioning (future)

## Monitoring & Observability

### Health Checks
- `/actuator/health` - Overall health
- `/actuator/health/liveness` - Kubernetes liveness
- `/actuator/health/readiness` - Kubernetes readiness

### Metrics
- `/actuator/metrics` - Prometheus metrics
- `/actuator/prometheus` - Prometheus endpoint

### Logging
- Structured JSON logging
- Centralized log aggregation
- Log levels: ERROR, WARN, INFO, DEBUG

## Technology Decisions

### Why Spring Boot?
- Mature ecosystem
- Excellent documentation
- Strong security (Spring Security)
- Easy integration with PostgreSQL
- Built-in monitoring (Actuator)

### Why React?
- Component-based architecture
- Strong TypeScript support
- Large ecosystem
- Excellent tooling (Vite)
- Material-UI for professional UI

### Why PostgreSQL?
- ACID compliance
- Strong data integrity
- JSON support
- Full-text search
- Excellent performance

### Why Redis?
- Fast in-memory cache
- Session management
- Pub/sub capabilities
- TTL support
- Cluster support

## Future Enhancements

- [ ] Microservices migration
- [ ] GraphQL API
- [ ] Real-time notifications (WebSocket)
- [ ] Mobile apps (React Native)
- [ ] AI/ML integration
- [ ] HL7 FHIR support
- [ ] Kubernetes deployment
- [ ] Multi-tenancy
