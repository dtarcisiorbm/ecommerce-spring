# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- **Customer Management System**
  - Complete customer authentication and registration system
  - CustomerController with POST /customers endpoint
  - AuthenticateCustomerUseCase for customer login validation
  - CreateCustomerUseCase for new customer registration
  - CustomerRequest DTO with comprehensive validation
  - CustomerGateway interface for data access operations
  - CustomerConfig Spring configuration with dependency injection
  - Enhanced SecurityConfig for customer endpoint access
  - Customer domain model with password support
  - CustomerEntity JPA entity with database mapping

- **Documentation Workflow**
  - Professional documentation management workflow
  - Comprehensive templates for README, DOCUMENTACAO, and CHANGELOG
  - Git automation scripts and Conventional Commits guidelines
  - Pull Request templates and checklists
  - Release automation scripts

### Changed
- Updated OrderRequest to include customer relationship
- Enhanced customer authentication flow
- Improved security configuration for customer endpoints
- Updated project structure to support customer management

## [0.0.2-SNAPSHOT] - 2026-03-30

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

## Recent Commits

- `c65837e` Merge remote-tracking branch 'origin/master'
- `f1ba51a` Fix OrderItemRequest visibility issue
- `e4af8d3` feat: reorganize use cases and add product management features
- `4f68f27` Add documentation for E-commerce Backend application
- `57af985` readme
- `e080886` fix:repository
- `0467716` fix:repository
- `6414533` fix:uses cases
- `b99acf9` feat: add order management system and fix authentication beans
- `14fa229` feat: spring security

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
