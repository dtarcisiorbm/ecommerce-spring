# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- CHANGELOG.md file to track project changes and improve team clarity

## [0.0.1-SNAPSHOT] - 2025-03-25

### Added
- **E-commerce Backend System** - Complete Spring Boot application with clean architecture
- **User Management** - User registration, authentication with JWT tokens
- **Product Management** - Product creation with SKU validation and stock control
- **Order Management** - Order creation with inventory validation and status tracking
- **Security Layer** - Spring Security configuration with JWT authentication
- **Database Integration** - PostgreSQL with JPA entities and repositories
- **Clean Architecture** - Separation of concerns with Core, Infrastructure, and Entrypoint layers
- **DTOs and Mappers** - Data transfer objects with MapStruct for entity conversion
- **Docker Support** - Docker Compose configuration for PostgreSQL

### Features
- **Authentication System**
  - User registration with email validation
  - Password hashing with BCrypt
  - JWT token generation and validation
  - Role-based access control (USER, ADMIN)
  
- **Product Catalog**
  - Product creation with unique SKU validation
  - Stock management and availability checking
  - Price and inventory tracking
  
- **Order Processing**
  - Order creation with multiple items
  - Automatic stock validation before order creation
  - Order status tracking (PENDING, PAID, SHIPPED, DELIVERED, CANCELED)
  - Total order calculation
  
- **Technical Features**
  - Clean Architecture with dependency inversion
  - Gateway pattern for external integrations
  - Use Cases for business logic orchestration
  - Bean Validation for input validation
  - Transactional data management
  - Comprehensive error handling

### Technical Stack
- Java 21
- Spring Boot 4.0.4
- Spring Data JPA
- Spring Security
- PostgreSQL
- MapStruct 1.5.5
- JWT (Auth0)
- Lombok
- Docker Compose

### Architecture Highlights
- **Core Layer**: Domain entities, business rules, and use cases
- **Infrastructure Layer**: Data providers, repositories, and external adapters
- **Entrypoint Layer**: REST controllers, DTOs, and mappers
- **Security**: JWT-based stateless authentication
- **Database**: Relational model with proper entity relationships

---

## Why This Changelog Matters

This changelog serves as the **single source of truth** for understanding what has changed in our e-commerce backend system. Unlike commit messages that focus on *how* something was implemented, this changelog explains:

- **What actually changed** - Features, fixes, and improvements
- **Why it matters** - Business impact and technical significance  
- **What impact it has** - How changes affect users, developers, and the system

### Benefits for the Team

✅ **New developers** can quickly understand the project evolution  
✅ **Stakeholders** can track feature delivery without reading commits  
✅ **Reporting** becomes easier with organized release information  
✅ **Re-work prevention** through better context awareness  
✅ **Professional release management** with clear version tracking  

### How to Maintain This Changelog

1. **Update before merging** - Add changes to the Unreleased section
2. **Be specific** - Include both technical and business context
3. **Follow the format** - Use Added, Changed, Deprecated, Removed, Fixed, Security
4. **Release regularly** - Move items from Unreleased to versioned sections
5. **Think before coding** - Knowing you'll document changes encourages better decisions

---

*This changelog follows industry best practices and helps maintain clarity as the team scales.*
