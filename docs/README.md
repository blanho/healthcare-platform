# Healthcare Platform Documentation

> Comprehensive documentation for the Healthcare Management System

## Overview

The Healthcare Platform is a modern, HIPAA-compliant healthcare management system built with Spring Boot and React. It provides comprehensive features for patient management, appointments, billing, medical records, and more.

## Architecture

- **Backend:** Spring Boot 3.4.2 with Java 21
- **Frontend:** React 18 with TypeScript and Material-UI
- **Database:** PostgreSQL 16 with Flyway migrations
- **Cache:** Redis 7
- **Storage:** S3-compatible (DigitalOcean Spaces)
- **Authentication:** JWT-based with Spring Security
- **Deployment:** Docker containers on DigitalOcean

## Documentation Index

### Getting Started
- [Development Setup](DEVELOPMENT.md) - Local environment setup
- [Deployment Guide](../DEPLOYMENT.md) - Production deployment to DigitalOcean

### Architecture & Design
- [System Architecture](ARCHITECTURE.md) - Overall system design and components
- [Backend Modules](MODULES.md) - Spring Boot module breakdown
- [Frontend Structure](FRONTEND.md) - React application architecture
- [Database Schema](DATABASE.md) - Database design and migrations

### Development
- [API Documentation](API.md) - REST API endpoints and contracts
- [Security Guide](SECURITY.md) - Security implementation and HIPAA compliance
- [Testing Guide](TESTING.md) - Unit, integration, and E2E testing
- [Code Style Guide](CODE_STYLE.md) - Coding standards and best practices

### Operations
- [Monitoring](MONITORING.md) - Logging, metrics, and alerts
- [Backup & Recovery](BACKUP.md) - Data backup procedures
- [Troubleshooting](TROUBLESHOOTING.md) - Common issues and solutions

## Quick Links

- **GitHub Repository:** https://github.com/your-org/healthcare-platform
- **Production:** https://yourdomain.com
- **Staging:** https://staging.yourdomain.com
- **API Docs:** https://api.yourdomain.com/swagger-ui.html

## Tech Stack

### Backend
- Spring Boot 3.4.2
- Spring Security + JWT
- Spring Data JPA
- Flyway Database Migrations
- PostgreSQL 16
- Redis 7
- MapStruct for DTO mapping
- Lombok for boilerplate reduction

### Frontend
- React 18
- TypeScript 5
- Vite 7.3
- Material-UI v6
- React Query (TanStack Query)
- Zustand for state management
- React Router v7

### Infrastructure
- Docker & Docker Compose
- DigitalOcean App Platform / Droplets
- Managed PostgreSQL & Redis
- DigitalOcean Spaces (S3-compatible)
- GitHub Actions for CI/CD

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for development workflow and guidelines.

## License

Proprietary - All rights reserved
