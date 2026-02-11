# =============================================
# Healthcare Platform Dockerfile
# Multi-stage build for optimal image size
# =============================================

# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom files first (for caching)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY healthcare-common/pom.xml healthcare-common/
COPY healthcare-patient/pom.xml healthcare-patient/
COPY healthcare-provider/pom.xml healthcare-provider/
COPY healthcare-appointment/pom.xml healthcare-appointment/
COPY healthcare-medical-record/pom.xml healthcare-medical-record/
COPY healthcare-billing/pom.xml healthcare-billing/
COPY healthcare-notification/pom.xml healthcare-notification/
COPY healthcare-auth/pom.xml healthcare-auth/
COPY healthcare-audit/pom.xml healthcare-audit/
COPY healthcare-app/pom.xml healthcare-app/

# Download dependencies (cached if pom.xml unchanged)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY . .

# Build the application
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

# Security: Run as non-root user
RUN addgroup -g 1001 healthcare && \
    adduser -u 1001 -G healthcare -s /bin/sh -D healthcare

WORKDIR /app

# Copy the layered JAR
COPY --from=builder /app/healthcare-app/target/*.jar app.jar

# Change ownership
RUN chown -R healthcare:healthcare /app

USER healthcare

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# JVM tuning for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
