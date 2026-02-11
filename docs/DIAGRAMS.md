# Architecture Diagrams

This document contains various architecture and workflow diagrams for the Healthcare Platform.

## System Architecture

```mermaid
graph TB
    subgraph "Client Layer"
        A[React Frontend<br/>Material-UI]
    end
    
    subgraph "API Gateway"
        B[Spring Boot<br/>Port 8080]
    end
    
    subgraph "Security Layer"
        C[JWT Filter]
        D[Spring Security]
    end
    
    subgraph "Application Layer"
        E[Patient Module]
        F[Appointment Module]
        G[Billing Module]
        H[Medical Record Module]
        I[Provider Module]
        J[Auth Module]
        K[Notification Module]
        L[Audit Module]
    end
    
    subgraph "Data Layer"
        M[(PostgreSQL<br/>Database)]
        N[(Redis<br/>Cache)]
        O[S3/Spaces<br/>Storage]
    end
    
    A -->|HTTPS/REST| B
    B --> C
    C --> D
    D --> E
    D --> F
    D --> G
    D --> H
    D --> I
    D --> J
    D --> K
    D --> L
    
    E --> M
    F --> M
    G --> M
    H --> M
    I --> M
    J --> M
    K --> M
    L --> M
    
    E --> N
    F --> N
    
    H --> O
    
    style A fill:#61dafb
    style B fill:#6db33f
    style M fill:#336791
    style N fill:#dc382d
```

## Request Flow

```mermaid
sequenceDiagram
    participant U as User Browser
    participant F as React Frontend
    participant A as API Gateway
    participant S as Spring Security
    participant SV as Service Layer
    participant R as Repository
    participant DB as PostgreSQL
    participant C as Redis Cache

    U->>F: User Action
    F->>A: HTTP Request + JWT
    A->>S: Validate Token
    S->>S: Extract User & Roles
    
    alt Token Valid
        S->>SV: Authorized Request
        SV->>C: Check Cache
        
        alt Cache Hit
            C-->>SV: Return Cached Data
        else Cache Miss
            SV->>R: Query Database
            R->>DB: SQL Query
            DB-->>R: Result Set
            R-->>SV: Entity
            SV->>C: Update Cache
        end
        
        SV-->>A: DTO Response
        A-->>F: JSON Response
        F-->>U: Update UI
    else Token Invalid
        S-->>A: 401 Unauthorized
        A-->>F: Error Response
        F-->>U: Redirect to Login
    end
```

## Authentication Flow

```mermaid
sequenceDiagram
    participant U as User
    participant F as Frontend
    participant A as Auth Controller
    participant S as Auth Service
    participant DB as Database
    participant J as JWT Service

    U->>F: Enter Credentials
    F->>A: POST /api/auth/login
    A->>S: authenticate(username, password)
    S->>DB: findByUsername()
    DB-->>S: User Entity
    S->>S: validatePassword(BCrypt)
    
    alt Password Valid
        S->>J: generateToken(user)
        J-->>S: JWT Token
        S-->>A: AuthResponse + Token
        A-->>F: 200 OK + Token
        F->>F: Store Token (localStorage)
        F-->>U: Redirect to Dashboard
    else Password Invalid
        S-->>A: AuthenticationException
        A-->>F: 401 Unauthorized
        F-->>U: Show Error
    end
```

## Appointment Booking Workflow

```mermaid
stateDiagram-v2
    [*] --> Searching: User searches provider
    Searching --> SelectingProvider: Provider found
    SelectingProvider --> CheckingAvailability: Select provider
    CheckingAvailability --> SelectingSlot: Available slots loaded
    SelectingSlot --> Confirming: User selects slot
    Confirming --> Creating: User confirms details
    Creating --> NotificationSent: Appointment created
    NotificationSent --> Scheduled: Email/SMS sent
    
    Scheduled --> Confirmed: Provider confirms
    Scheduled --> Cancelled: User/Provider cancels
    Confirmed --> Completed: Appointment completed
    Confirmed --> NoShow: Patient didn't show
    
    Completed --> [*]
    Cancelled --> [*]
    NoShow --> [*]
    
    CheckingAvailability --> Searching: No slots available
```

## Data Model ER Diagram

```mermaid
erDiagram
    PATIENTS ||--o{ APPOINTMENTS : books
    PROVIDERS ||--o{ APPOINTMENTS : schedules
    APPOINTMENTS ||--o{ MEDICAL_RECORDS : generates
    APPOINTMENTS ||--o{ INVOICES : bills
    PATIENTS ||--o{ INVOICES : owes
    PATIENTS ||--o{ MEDICAL_RECORDS : has
    PROVIDERS ||--o{ MEDICAL_RECORDS : creates
    USERS ||--o{ ROLES : has
    USERS ||--o{ AUDIT_EVENTS : generates
    
    PATIENTS {
        bigint id PK
        string first_name
        string last_name
        date date_of_birth
        string email UK
        string phone
        string status
        timestamp created_at
    }
    
    PROVIDERS {
        bigint id PK
        string first_name
        string last_name
        string specialization
        string license_number
        string status
    }
    
    APPOINTMENTS {
        bigint id PK
        bigint patient_id FK
        bigint provider_id FK
        datetime scheduled_date
        time start_time
        time end_time
        string status
        string reason
    }
    
    MEDICAL_RECORDS {
        bigint id PK
        bigint patient_id FK
        bigint provider_id FK
        bigint appointment_id FK
        string diagnosis
        string treatment
        text notes
    }
    
    INVOICES {
        bigint id PK
        bigint patient_id FK
        bigint appointment_id FK
        decimal total_amount
        decimal paid_amount
        string status
        date due_date
    }
    
    USERS {
        bigint id PK
        string username UK
        string password
        string email UK
        string status
    }
    
    AUDIT_EVENTS {
        bigint id PK
        bigint user_id FK
        string action
        string entity
        timestamp event_timestamp
    }
```

## Module Dependencies

```mermaid
graph TD
    APP[healthcare-app]
    COMMON[healthcare-common]
    PATIENT[healthcare-patient]
    PROVIDER[healthcare-provider]
    APPT[healthcare-appointment]
    BILLING[healthcare-billing]
    MR[healthcare-medical-record]
    AUTH[healthcare-auth]
    NOTIF[healthcare-notification]
    AUDIT[healthcare-audit]
    LOC[healthcare-location]
    
    APP --> PATIENT
    APP --> PROVIDER
    APP --> APPT
    APP --> BILLING
    APP --> MR
    APP --> AUTH
    APP --> NOTIF
    APP --> AUDIT
    APP --> LOC
    
    PATIENT --> COMMON
    PROVIDER --> COMMON
    AUTH --> COMMON
    NOTIF --> COMMON
    AUDIT --> COMMON
    LOC --> COMMON
    
    APPT --> COMMON
    APPT --> PATIENT
    APPT --> PROVIDER
    APPT --> LOC
    APPT --> NOTIF
    
    BILLING --> COMMON
    BILLING --> PATIENT
    BILLING --> APPT
    BILLING --> NOTIF
    
    MR --> COMMON
    MR --> PATIENT
    MR --> PROVIDER
    MR --> APPT
    MR --> AUDIT
    
    style APP fill:#6db33f
    style COMMON fill:#ffd700
    style PATIENT fill:#61dafb
    style PROVIDER fill:#61dafb
    style APPT fill:#61dafb
```

## Deployment Architecture

```mermaid
graph TB
    subgraph "DigitalOcean"
        subgraph "Load Balancer"
            LB[Load Balancer<br/>SSL Termination]
        end
        
        subgraph "App Platform"
            FE1[Frontend Container<br/>Nginx]
            BE1[Backend Container<br/>Java 21]
            BE2[Backend Container<br/>Java 21]
        end
        
        subgraph "Managed Services"
            DB[(PostgreSQL<br/>Managed Database)]
            REDIS[(Redis<br/>Managed Cache)]
            S3[Spaces<br/>Object Storage]
        end
    end
    
    USER[Users] -->|HTTPS| LB
    LB --> FE1
    LB --> BE1
    LB --> BE2
    
    FE1 -->|API Calls| BE1
    FE1 -->|API Calls| BE2
    
    BE1 --> DB
    BE2 --> DB
    BE1 --> REDIS
    BE2 --> REDIS
    BE1 --> S3
    BE2 --> S3
    
    style USER fill:#ff6b6b
    style LB fill:#4ecdc4
    style FE1 fill:#61dafb
    style BE1 fill:#6db33f
    style BE2 fill:#6db33f
    style DB fill:#336791
    style REDIS fill:#dc382d
```

## Security Flow

```mermaid
flowchart TD
    A[Client Request] --> B{Has JWT Token?}
    B -->|No| C[Return 401 Unauthorized]
    B -->|Yes| D{Token Valid?}
    D -->|No| C
    D -->|Yes| E{Token Expired?}
    E -->|Yes| C
    E -->|No| F{Has Required Role?}
    F -->|No| G[Return 403 Forbidden]
    F -->|Yes| H{Resource Owner?}
    H -->|No| I{Admin Role?}
    I -->|No| G
    I -->|Yes| J[Process Request]
    H -->|Yes| J
    J --> K[Log Audit Event]
    K --> L[Return Response]
    
    style A fill:#61dafb
    style C fill:#ff6b6b
    style G fill:#ff6b6b
    style L fill:#51cf66
```

---

## How to View These Diagrams

### GitHub
Mermaid diagrams render automatically on GitHub when viewing markdown files.

### VS Code
Install the "Markdown Preview Mermaid Support" extension to preview diagrams locally.

### Export as Images
Use tools like:
- [Mermaid Live Editor](https://mermaid.live) - Export as PNG/SVG
- [Draw.io](https://app.diagrams.net) - For custom diagrams
- [Excalidraw](https://excalidraw.com) - For hand-drawn style

## Creating Custom Diagrams

For more complex diagrams, use:
1. **Draw.io** - Desktop app or web version
2. **Lucidchart** - Professional diagramming
3. **Figma** - Design tool with diagram capabilities
4. **PlantUML** - Text-based UML diagrams

Save custom diagrams as images in `docs/images/` folder.
