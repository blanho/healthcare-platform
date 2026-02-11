# Development Setup Guide

## Prerequisites

### Required Software

- **Java 21** (Eclipse Temurin recommended)
- **Maven 3.9+**
- **Node.js 20+** and npm
- **PostgreSQL 16**
- **Redis 7**
- **Docker & Docker Compose** (for local services)
- **Git**

### Recommended Tools

- **IDE:** IntelliJ IDEA Ultimate or VS Code
- **Database Client:** pgAdmin, DBeaver, or DataGrip
- **API Testing:** Postman or Insomnia
- **Redis Client:** RedisInsight

## Initial Setup

### 1. Clone Repository

```bash
git clone https://github.com/your-org/healthcare-platform.git
cd healthcare-platform
```

### 2. Start Local Services

```bash
# Start PostgreSQL, Redis, and other services
docker compose -f deploy/docker-compose.yml up -d

# Verify services are running
docker compose -f deploy/docker-compose.yml ps
```

Services available:
- PostgreSQL: `localhost:5432`
- Redis: `localhost:6379`
- pgAdmin: `http://localhost:5050`
- MailHog: `http://localhost:8025`
- MinIO: `http://localhost:9001`
- LocalStack: `localhost:4566`

### 3. Configure Backend

```bash
cd src

# Create local properties (optional)
cp healthcare-app/src/main/resources/application.properties \
   healthcare-app/src/main/resources/application-local.properties
```

Edit `application-local.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/healthcare
spring.datasource.username=healthcare
spring.datasource.password=healthcare123

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# JWT
jwt.secret=your-local-secret-key-min-256-bits
jwt.expiration=3600000

# AWS/LocalStack
aws.region=us-east-1
aws.s3.endpoint=http://localhost:4566
aws.s3.bucket=healthcare-documents
aws.access-key-id=test
aws.secret-access-key=test

# SMTP (MailHog)
spring.mail.host=localhost
spring.mail.port=1025
```

### 4. Build Backend

```bash
# Clean and compile
./mvnw clean compile

# Run tests
./mvnw test

# Package (skip tests for speed)
./mvnw package -DskipTests
```

### 5. Run Database Migrations

```bash
# Migrations run automatically on startup
# Or manually with Flyway:
./mvnw flyway:migrate
```

### 6. Start Backend

```bash
# Option 1: Maven
./mvnw spring-boot:run -pl healthcare-app

# Option 2: JAR
java -jar healthcare-app/target/healthcare-app-0.0.1-SNAPSHOT.jar

# Option 3: IDE
# Run HealthcareApplication.java main method
```

Backend will start on `http://localhost:8080`

### 7. Configure Frontend

```bash
cd ../web

# Install dependencies
npm install

# Create local env file
cp .env.example .env.local
```

Edit `.env.local`:

```env
VITE_API_URL=http://localhost:8080
VITE_ENV=development
```

### 8. Start Frontend

```bash
# Development server with hot reload
npm run dev
```

Frontend will start on `http://localhost:5173`

## Development Workflow

### Backend Development

#### Module Structure

```
healthcare-{module}/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/healthcare/{module}/
│   │           ├── controller/
│   │           ├── service/
│   │           ├── repository/
│   │           ├── entity/
│   │           └── dto/
│   └── test/
│       └── java/
└── pom.xml
```

#### Adding a New REST Endpoint

1. **Create DTO:**
```java
@Data
public class PatientRequest {
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    @Email
    private String email;
}
```

2. **Create Service Method:**
```java
@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository repository;
    
    public PatientResponse createPatient(PatientRequest request) {
        Patient patient = new Patient();
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        return repository.save(patient);
    }
}
```

3. **Create Controller:**
```java
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService service;
    
    @PostMapping
    public ResponseEntity<PatientResponse> create(
        @Valid @RequestBody PatientRequest request
    ) {
        return ResponseEntity.ok(service.createPatient(request));
    }
}
```

#### Database Migration

Create new migration in `src/main/resources/db/migration/`:

```sql
-- V1__create_patients_table.sql
CREATE TABLE patients (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_patients_email ON patients(email);
```

#### Running Tests

```bash
# All tests
./mvnw test

# Specific module
./mvnw test -pl healthcare-patient

# Integration tests only
./mvnw verify -P integration-tests

# With coverage
./mvnw test jacoco:report
open target/site/jacoco/index.html
```

### Frontend Development

#### Component Structure

```
src/
├── components/          # Reusable UI components
│   ├── common/
│   ├── forms/
│   └── layout/
├── features/           # Feature-specific components
│   ├── patients/
│   ├── appointments/
│   └── billing/
├── pages/              # Page components
├── hooks/              # Custom React hooks
├── stores/             # Zustand stores
├── lib/                # API clients and utilities
└── types/              # TypeScript types
```

#### Creating a New Feature

1. **Create types:**
```typescript
// types/patient.ts
export interface Patient {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
}
```

2. **Create API client:**
```typescript
// lib/api/patients.ts
import { api } from './client';

export const patientsApi = {
  getAll: () => api.get<Patient[]>('/api/patients'),
  getById: (id: number) => api.get<Patient>(`/api/patients/${id}`),
  create: (data: CreatePatientRequest) => 
    api.post<Patient>('/api/patients', data),
};
```

3. **Create component:**
```typescript
// features/patients/PatientList.tsx
export function PatientList() {
  const { data, isLoading } = useQuery({
    queryKey: ['patients'],
    queryFn: () => patientsApi.getAll(),
  });

  if (isLoading) return <CircularProgress />;

  return (
    <List>
      {data?.map(patient => (
        <ListItem key={patient.id}>
          {patient.firstName} {patient.lastName}
        </ListItem>
      ))}
    </List>
  );
}
```

#### Running Frontend Tests

```bash
# Unit tests
npm test

# Coverage
npm run test:coverage

# Type checking
npm run type-check

# Linting
npm run lint
npm run lint:fix
```

## Debugging

### Backend Debugging

**IntelliJ IDEA:**
1. Set breakpoints in code
2. Run → Debug 'HealthcareApplication'

**VS Code:**
1. Install Java Extension Pack
2. Use provided launch configuration
3. F5 to start debugging

**Remote Debugging:**
```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 \
  -jar healthcare-app/target/healthcare-app-0.0.1-SNAPSHOT.jar
```

### Frontend Debugging

**Browser DevTools:**
- React Developer Tools extension
- Redux DevTools for state inspection

**VS Code:**
1. Install Debugger for Chrome
2. F5 to attach to browser
3. Set breakpoints in TypeScript files

## Database Management

### Connect to Local Database

```bash
# Via psql
psql -h localhost -p 5432 -U healthcare -d healthcare

# Via pgAdmin
# URL: http://localhost:5050
# Email: admin@healthcare.com
# Password: admin
```

### Common Database Tasks

```sql
-- List all tables
\dt

-- View schema
\d+ patients

-- Check Flyway history
SELECT * FROM flyway_schema_history;

-- Reset database (destructive!)
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
```

### Database Seeding

```bash
# Run seed script
./mvnw spring-boot:run -Dspring-boot.run.arguments=--seed
```

## API Testing

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

### Authentication

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Use token
TOKEN="your-jwt-token"
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/patients
```

### Swagger UI

Access interactive API documentation:
```
http://localhost:8080/swagger-ui.html
```

## Code Quality

### Run All Checks

```bash
# Backend
./mvnw verify checkstyle:check spotbugs:check pmd:check

# Frontend
npm run lint
npm run type-check
npm test
```

### Code Formatting

```bash
# Backend (Spotless)
./mvnw spotless:apply

# Frontend (Prettier)
npm run format
```

## Environment Profiles

### Backend Profiles

- `default` - Development (application.properties)
- `local` - Local overrides (application-local.properties)
- `prod` - Production (application-prod.properties)

```bash
# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### Frontend Modes

- `development` - Local dev with hot reload
- `production` - Optimized build

```bash
# Development
npm run dev

# Production build
npm run build
npm run preview
```

## Troubleshooting

### Port Already in Use

```bash
# Find process using port 8080
lsof -i :8080

# Kill process
kill -9 <PID>
```

### Database Connection Issues

```bash
# Check PostgreSQL is running
docker compose ps postgres

# Check logs
docker compose logs postgres

# Restart service
docker compose restart postgres
```

### Redis Connection Issues

```bash
# Test Redis connection
redis-cli -h localhost -p 6379 ping

# Clear Redis cache
redis-cli FLUSHALL
```

### Maven Build Issues

```bash
# Clear Maven cache
./mvnw dependency:purge-local-repository

# Force update dependencies
./mvnw clean install -U
```

### NPM Issues

```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

## Next Steps

- Read [API Documentation](API.md)
- Review [Code Style Guide](CODE_STYLE.md)
- Understand [Security Implementation](SECURITY.md)
- Learn [Testing Practices](TESTING.md)
