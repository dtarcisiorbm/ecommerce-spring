# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- **Enhanced Security Error Handling**
  - Comprehensive 401/403 error responses with detailed English messages
  - Authentication entry point for missing/invalid tokens with clear guidance
  - Access denied handler for permission-based errors with explanations
  - Enhanced SecurityExceptionHandler with multiple exception types
  - Professional error response format with error, message, details, timestamp, and path
  - Clear developer guidance for authentication and authorization issues

### Security
- **Authentication Error Messages** - Detailed English messages for missing tokens, invalid credentials, and access denied scenarios
- **Developer Experience** - Clear error responses help developers understand and fix authentication issues quickly
- **Audit Logging** - Enhanced security logging for failed authentication attempts with IP and path tracking

### Changed
- **Error Response Format** - Now includes timestamp and path fields for better debugging
- **Controller Validation** - Replaced manual null checks with proper validation annotations
- **Repository Operations** - Added existence checks before delete operations

### Fixed
- **Repository Safety** - CustomerDataProvider.deleteById() now validates entity existence before deletion
- **Authentication Validation** - AuthController endpoints use proper validation instead of manual checks
- **Exception Propagation** - Controllers now properly handle and re-throw business logic exceptions

### Security
- **Authentication Logging** - Added security audit logging for failed authentication attempts
- **Access Control** - Enhanced AccessDeniedException handling with proper logging
- **Input Validation** - Improved request validation with @NotBlank annotations

### Added
- **Comprehensive Test Suite**
  - Complete test coverage with JUnit 5 and Mockito
  - Unit tests for domain layer (ProductTest)
  - Unit tests for use cases (CreateProductUseCaseTest, ListProductsUseCaseTest)
  - Unit tests for data provider (ProductDataProviderTest)
  - Integration tests for controller (ProductControllerTest)
  - Integration tests for repository (ProductRepositoryTest)
  - Test documentation with comprehensive coverage report
  - AAA pattern implementation (Arrange, Act, Assert)
  - Success, error, and edge case scenarios
  - >90% test coverage achievement

### Fixed
- **PageImpl Serialization Warning**
  - Added @EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO) to BackendApplication
  - Resolved unstable JSON structure warning for pagination responses
  - Ensured stable pagination serialization format

### Changed
- **Spring Boot 4.x Compatibility**
  - Updated @MockBean to @MockitoBean for better clarity
  - Updated @WebMvcTest import to org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
  - Updated @DataJpaTest import to org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
  - Updated @TestEntityManager import to org.springframework.boot.jpa.test.autoconfigure.TestEntityManager

### Added
- **Customer CRUD Operations**
  - Complete CRUD operations for customer management
  - DeleteCustomerUseCase for customer deletion
  - FindCustomerByIdUseCase for customer search by ID
  - UpdateCustomerUseCase for customer data updates
  - ListCustomersUseCase for paginated customer listing
  - Enhanced CustomerGateway with full CRUD interface
  - CustomerDataProvider implementation with all operations

### Fixed
- **Customer Repository Type Mismatch**
  - Corrected CustomerRepository ID type from Long to UUID
  - Fixed deleteById method parameter type compatibility
  - Added UUID import to CustomerRepository interface

### Changed
- Updated Customer domain model to use UUID as ID type
- Enhanced customer data provider with comprehensive operations
- Improved type consistency across customer-related components

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

- `fbd0c27` feat: add comprehensive exception handling
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
