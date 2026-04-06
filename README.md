# Documentação da Aplicação E-commerce Backend

## Visão Geral

Esta é uma aplicação backend de e-commerce desenvolvida com Spring Boot 4.0.4 e Java 21, seguindo uma arquitetura limpa com separação clara de responsabilidades. A aplicação utiliza PostgreSQL como banco de dados, Redis para cache e rate limiting, e implementa autenticação via JWT com role-based authorization.

## Stack Tecnológico

- **Java 21**
- **Spring Boot 4.0.4**
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Segurança e autenticação
- **PostgreSQL** - Banco de dados relacional
- **Redis** - Cache e rate limiting
- **Docker Compose** - Containerização do banco de dados
- **MapStruct 1.5.5** - Mapeamento entre entidades e domínios
- **JWT (Auth0)** - Tokens de autenticação
- **Lombok** - Redução de código boilerplate
- **Maven** - Gerenciamento de dependências
- **Bucket4j** - Rate limiting avançado

## Estrutura do Projeto

```
src/main/java/com/ecommerce_backend/backend/
├── BackendApplication.java          # Classe principal da aplicação
├── core/                           # Camada de Domínio (Core)
│   ├── domain/                     # Entidades de negócio
│   │   ├── Category.java
│   │   ├── Customer.java
│   │   ├── Notification.java
│   │   ├── NotificationStatus.java
│   │   ├── NotificationType.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── OrderStatus.java
│   │   ├── Payment.java
│   │   ├── PaymentMethod.java
│   │   ├── PaymentStatus.java
│   │   ├── Product.java
│   │   ├── ShoppingCart.java
│   │   ├── ShoppingCartItem.java
│   │   ├── UserRole.java
│   │   └── User.java
│   ├── gateway/                    # Interfaces de integração
│   │   ├── CategoryGateway.java
│   │   ├── CustomerGateway.java
│   │   ├── NotificationGateway.java
│   │   ├── OrderGateway.java
│   │   ├── PasswordHasherGateway.java
│   │   ├── PaymentGateway.java
│   │   ├── ProductGateway.java
│   │   ├── ShoppingCartGateway.java
│   │   ├── TokenServiceGateway.java
│   │   └── UserGateway.java
│   └── useCases/                   # Casos de uso (regras de negócio)
│       ├── cart/                    # Casos de uso do carrinho
│       │   ├── AddCartItemUseCase.java
│       │   ├── ClearCartUseCase.java
│       │   ├── GetCartUseCase.java
│       │   ├── GetOrCreateCartUseCase.java
│       │   ├── RemoveCartItemUseCase.java
│       │   └── UpdateCartItemUseCase.java
│       ├── categories/              # Casos de uso de categorias
│       │   ├── CreateCategoryUseCase.java
│       │   ├── DeleteCategoryUseCase.java
│       │   ├── FindCategoryByIdUseCase.java
│       │   ├── ListActiveCategoriesUseCase.java
│       │   ├── ListCategoriesUseCase.java
│       │   ├── ListRootCategoriesUseCase.java
│       │   ├── ListSubcategoriesUseCase.java
│       │   └── UpdateCategoryUseCase.java
│       ├── create/                  # Casos de uso de criação
│       │   ├── AuthenticateCustomerUseCase.java
│       │   ├── AuthenticateUserUseCase.java
│       │   ├── CreateCustomerUseCase.java
│       │   ├── CreateOrderUseCase.java
│       │   ├── CreateProductUseCase.java
│       │   └── CreateUserUseCase.java
│       ├── delete/                  # Casos de uso de exclusão
│       │   ├── DeleteProductUseCase.java
│       │   └── FindCustomerByIdUseCase.java
│       ├── find/                    # Casos de uso de busca
│       │   ├── FindProductByIdUseCase.java
│       │   └── SearchProductsUseCase.java
│       ├── list/                    # Casos de uso de listagem
│       │   ├── ListOrdersUseCase.java
│       │   ├── ListProductsUseCase.java
│       │   └── ListUsersUseCase.java
│       ├── payment/                 # Casos de uso de pagamentos
│       │   ├── ProcessPaymentUseCase.java
│       │   └── RefundPaymentUseCase.java
│       └── update/                  # Casos de uso de atualização
│           ├── UpdateCategoryUseCase.java
│           ├── UpdateCustomerUseCase.java
│           ├── UpdateOrderStatusUseCase.java
│           └── UpdateProductUseCase.java
├── entrypoints/                    # Camada de Apresentação
│   ├── controller/                 # Controllers REST
│   │   ├── AuthController.java
│   │   ├── CategoryController.java
│   │   ├── CustomerController.java
│   │   ├── OrderController.java
│   │   ├── PaymentController.java
│   │   ├── ProductController.java
│   │   └── ShoppingCartController.java
│   ├── dto/                        # Data Transfer Objects
│   │   ├── AddCartItemRequest.java
│   │   ├── CartResponse.java
│   │   ├── CategoryRequest.java
│   │   ├── CustomerRequest.java
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   ├── OrderItemRequest.java
│   │   ├── OrderRequest.java
│   │   ├── OrderStatusUpdateRequest.java
│   │   ├── PaymentRequest.java
│   │   ├── ProductFilterRequest.java
│   │   ├── RefundRequest.java
│   │   ├── RegisterRequest.java
│   │   └── UpdateCartItemRequest.java
│   ├── exceptions/                 # Tratamento global de exceções
│   │   ├── GlobalExceptionHandler.java
│   │   └── SecurityExceptionHandler.java
│   └── mapper/                     # Mapeamento de DTOs
│       ├── CategoryMapper.java
│       ├── CustomerMapper.java
│       ├── NotificationMapper.java
│       ├── OrderMapper.java
│       ├── PaymentMapper.java
│       ├── ProductMapper.java
│       ├── ShoppingCartItemMapper.java
│       ├── ShoppingCartMapper.java
│       └── UserMapper.java
└── infrastructure/                 # Camada de Infraestrutura
    ├── cache/                       # Cache services
    │   ├── CacheService.java
    │   └── ProductCacheService.java
    ├── config/                       # Configurações Spring
    │   ├── CategoryConfig.java
    │   ├── CustomerConfig.java
    │   ├── OrderConfig.java
    │   ├── PaymentConfig.java
    │   ├── ProductConfig.java
    │   ├── RedisConfig.java
    │   ├── SecurityConfig.java
    │   ├── ShoppingCartConfig.java
    │   └── UserConfig.java
    ├── dataprovider/               # Implementações dos Gateways
    │   ├── CategoryDataProvider.java
    │   ├── NotificationDataProvider.java
    │   ├── OrderDataProvider.java
    │   ├── PaymentDataProvider.java
    │   ├── ProductDataProvider.java
    │   ├── ShoppingCartDataProvider.java
    │   ├── UserDataProvider.java
    │   └── repository/             # Repositórios Spring Data
    │       ├── CategoryRepository.java
    │       ├── CustomerRepository.java
    │       ├── NotificationRepository.java
    │       ├── OrderRepository.java
    │       ├── PaymentRepository.java
    │       ├── ProductRepository.java
    │       ├── ShoppingCartItemRepository.java
    │       ├── ShoppingCartRepository.java
    │       └── UserRepository.java
    ├── notification/                # Serviços de notificação
    │   └── EmailNotificationService.java
    ├── payment/                     # Gateways de pagamento
    │   └── StripePaymentGateway.java
    ├── persistence/                 # Entidades JPA
    │   └── entity/
    │       ├── CategoryEntity.java
    │       ├── CustomerEntity.java
    │       ├── NotificationEntity.java
    │       ├── OrderEntity.java
    │       ├── OrderItemEntity.java
    │       ├── PaymentEntity.java
    │       ├── ProductEntity.java
    │       ├── ShoppingCartEntity.java
    │       ├── ShoppingCartItemEntity.java
    │       └── UserEntity.java
    └── security/                   # Configurações de segurança
        ├── BCryptHasherAdapter.java
        ├── JwtTokenAdapter.java
        └── SecurityFilter.java
```

## 1. Camada de Domínio (Core)

### 1.1 Entidades de Negócio

#### **Customer.java**
```java
public record Customer(
    UUID id,
    String fullName,
    String email,
    String taxId, // CPF/CNPJ
    String password // Senha criptografada
) {}
```
- **Propósito**: Representa um cliente do sistema
- **Características**: Record imutável com informações básicas do cliente

#### **Product.java**
```java
public record Product(
    Long id,
    String name,
    String sku,
    BigDecimal price,
    Integer stock
) {
    public boolean hasStock(int quantity) {
        return this.stock >= quantity;
    }
}
```
- **Propósito**: Representa um produto do catálogo
- **Regra de Negócio**: Método `hasStock()` verifica disponibilidade em estoque
- **Validações**: SKU único, preço e estoque obrigatórios

#### **Order.java**
```java
public record Order(
    Long id,
    Customer customer,
    List<OrderItem> items,
    LocalDateTime createdAt,
    OrderStatus status
) {
    public BigDecimal getTotal() {
        return items.stream()
            .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```
- **Propósito**: Representa um pedido do cliente
- **Regra de Negócio**: Método `getTotal()` calcula valor total do pedido
- **Composição**: Contém múltiplos OrderItems

#### **OrderItem.java**
```java
public record OrderItem(
    Long id,
    Long productId,
    Integer quantity,
    BigDecimal unitPrice
) {}
```
- **Propósito**: Representa um item dentro de um pedido
- **Características**: Vincula produto, quantidade e preço unitário

#### **OrderStatus.java**
```java
public enum OrderStatus {
    PENDING,    // Aguardando pagamento
    PAID,       // Pagamento confirmado
    SHIPPED,    // Enviado
    DELIVERED,  // Entregue
    CANCELED    // Cancelado
}
```
- **Propósito**: Controla o ciclo de vida dos pedidos

#### **User.java**
```java
public record User(
    UUID id,
    String name,
    String email,
    String password, // Senha já criptografada
    Set<String> roles,
    boolean active
) {
    public boolean isInternalUser() {
        return email.endsWith("@empresa.com");
    }
}
```
- **Propósito**: Representa usuários do sistema
- **Regra de Negócio**: Método `isInternalUser()` identifica usuários internos
- **Segurança**: Senha armazenada criptografada

### 1.2 Interfaces Gateway

Definem contratos para integração com infraestrutura, seguindo o princípio Dependency Inversion:

- **ProductGateway**: Operações com produtos (save, findBySku, findById)
- **CustomerGateway**: Operações com clientes (save, findById, findByEmail, deleteById, findAll, update)
- **UserGateway**: Operações com usuários (save, findByEmail)
- **OrderGateway**: Operações com pedidos (save)
- **PasswordHasherGateway**: Criptografia de senhas (hash, matches)
- **TokenServiceGateway**: Geração e validação de tokens JWT

### 1.3 Casos de Uso (Use Cases)

#### **CreateProductUseCase**
- **Responsabilidade**: Criar novos produtos
- **Validação**: Impede duplicação de SKU
- **Fluxo**: Verifica SKU → Salva produto

#### **CreateCustomerUseCase**
- **Responsabilidade**: Registrar novos clientes
- **Validações**: Email único, CPF/CNPJ único, senha mínima 6 caracteres
- **Processo**: Criptografa senha → Salva cliente com role padrão "CUSTOMER"

#### **AuthenticateCustomerUseCase**
- **Responsabilidade**: Autenticar clientes
- **Validações**: Credenciais válidas, conta ativa
- **Fluxo**: Busca cliente → Verifica senha → Retorna cliente autenticado

#### **DeleteCustomerUseCase**
- **Responsabilidade**: Excluir clientes do sistema
- **Validações**: Verifica existência do cliente
- **Processo**: Busca cliente → Exclui permanentemente

#### **FindCustomerByIdUseCase**
- **Responsabilidade**: Buscar cliente por ID
- **Validações**: ID válido
- **Retorno**: Optional<Customer> com dados do cliente

#### **UpdateCustomerUseCase**
- **Responsabilidade**: Atualizar dados do cliente
- **Validações**: Cliente existe, email único (se alterado)
- **Processo**: Busca cliente → Atualiza dados → Salva

#### **ListCustomersUseCase**
- **Responsabilidade**: Listar clientes com paginação
- **Retorno**: Page<Customer> com paginação configurável
- **Parâmetros**: Pageable para controle de paginação
- **Uso**: Endpoint GET `/customers` com suporte a paginação

#### **CreateUserUseCase**
- **Responsabilidade**: Registrar novos usuários
- **Validações**: Email único, senha mínima 6 caracteres
- **Processo**: Criptografa senha → Define role padrão "USER" → Salva usuário

#### **AuthenticateUserUseCase**
- **Responsabilidade**: Autenticar usuários
- **Validações**: Credenciais válidas, conta ativa
- **Fluxo**: Busca usuário → Verifica senha → Retorna usuário autenticado

#### **CreateOrderUseCase**
- **Responsabilidade**: Criar pedidos
- **Validações**: Estoque disponível para todos os itens
- **Processo**: Valida estoque → Define status PENDING → Salva pedido

#### **ListOrdersUseCase**
- **Responsabilidade**: Listar todos os pedidos
- **Retorno**: Lista de Order com todos os itens
- **Uso**: Endpoint GET `/orders` para consulta de pedidos

#### **ListUsersUseCase**
- **Responsabilidade**: Listar todos os usuários do sistema
- **Retorno**: Lista de User com informações básicas
- **Uso**: Suporte a funcionalidades administrativas

#### **ListProductsUseCase**
- **Responsabilidade**: Listar produtos com paginação
- **Retorno**: Page<Product> com paginação configurável
- **Parâmetros**: Pageable para controle de paginação
- **Uso**: Endpoint GET `/products` com suporte a paginação

## 2. Camada de Infraestrutura

### 2.1 Configurações Spring

#### **SecurityConfig.java**
- **Propósito**: Configuração principal de segurança
- **Features**: Desabilita CSRF, sessão stateless, JWT filter
- **Endpoints Públicos**: `/auth/login`, `/auth/register`
- **Error Handling**: Authentication entry point e access denied handler personalizados
- **Bean**: PasswordEncoder (BCrypt), AuthenticationManager

**Enhanced Security Error Responses:**
- **401 Unauthorized**: Para tokens ausentes/inválidos com orientação detalhada
- **403 Forbidden**: Para permissões insuficientes com explicações claras
- **Formato Padrão**: JSON com error, message, details, timestamp, e path

#### **CustomerConfig.java** & **UserConfig.java** & **OrderConfig.java** & **ProductConfig.java**
- **Propósito**: Configuração de beans dos casos de uso
- **Implementação**: Injeção de dependências via @Bean
- **CustomerConfig**: Configura CreateCustomerUseCase e AuthenticateCustomerUseCase

### 2.2 Implementações Data Provider

#### **ProductDataProvider**
- Implementa ProductGateway
- Usa ProductRepository e ProductMapper
- Operações: save, findBySku, findById

#### **UserDataProvider**
- Implementa UserGateway
- Usa UserRepository e UserMapper
- Operações: save, findByEmail

#### **OrderDataProvider**
- Implementa OrderGateway
- Usa OrderRepository e OrderMapper
- **@Transactional**: Garante atomicidade na criação de pedidos
- **Relacionamento**: Vincula OrderItems ao Order pai

### 2.3 Entidades JPA

#### **CustomerEntity**
```java
@Entity
@Table(name = "customers")
public class CustomerEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String fullName;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(name = "tax_id", nullable = false, unique = true)
    private String taxId; // CPF/CNPJ
    
    @Column(nullable = false)
    private String password; // Campo para hash BCrypt
}
```
- **Propósito**: Persistência de dados de clientes
- **Validações**: Email e taxId únicos
- **Campos**: UUID auto-generated, nome completo, email, CPF/CNPJ, senha criptografada

#### **ProductEntity**
```java
@Entity
@Table(name = "products")
public class ProductEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String sku;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer stock;
}
```

#### **UserEntity**
```java
@Entity
@Table(name = "users")
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password; // Hash BCrypt
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles")
    private Set<String> roles;
    
    private boolean active = true;
}
```

#### **OrderEntity & OrderItemEntity**
- **Relacionamento**: @OneToMany entre Order e OrderItem
- **Cascade**: CascadeType.ALL para propagação de operações
- **Fetch**: LAZY para otimização de performance

### 2.4 Repositórios Spring Data

Interfaces que estendem JpaRepository:

- **CustomerRepository**: Busca por email customizada, operações CRUD completas
- **ProductRepository**: Busca por SKU customizada
- **UserRepository**: Busca por email customizada
- **OrderRepository**: Operações CRUD padrão

### 2.5 Camada de Segurança

#### **JwtTokenAdapter**
- Implementa TokenServiceGateway
- **Geração**: Token com expiração de 2 horas
- **Validação**: Verifica assinatura e issuer
- **Configuração**: Secret via application.properties

#### **BCryptHasherAdapter**
- Implementa PasswordHasherGateway
- **Hashing**: BCrypt com força padrão
- **Validação**: Verificação de senhas

#### **SecurityFilter**
- Extende OncePerRequestFilter
- **Fluxo**: Extrai token → Valida → Configura contexto Spring Security
- **Autenticação**: Cria UsernamePasswordAuthenticationToken

## 3. Camada de Apresentação (Entrypoints)

### 3.1 Controllers

#### **AuthController**
- **Endpoints**:
  - `POST /auth/register`: Registro de novos usuários
  - `POST /auth/login`: Autenticação e geração de token
- **Validações**: @Valid nos DTOs, Bean Validation
- **Respostas**: HTTP 201 para registro, 200 com token para login

#### **CustomerController**
- **Endpoints**:
  - `POST /customers`: Criar novo cliente
  - `GET /customers`: Listar clientes com paginação
  - `GET /customers/{id}`: Buscar cliente por ID
  - `PUT /customers/{id}`: Atualizar dados do cliente
  - `DELETE /customers/{id}`: Excluir cliente
  - `POST /auth/customer/login`: Autenticação de cliente
- **Validações**: @Valid nos DTOs, Bean Validation
- **Mapeamento**: Conversão manual de CustomerRequest para domínio Customer
- **Respostas**: HTTP 201 Created para criação, 200 para consultas e atualizações, 204 para exclusão

#### **OrderController**
- **Endpoints**:
  - `POST /orders`: Criar novo pedido
  - `GET /orders`: Listar todos os pedidos
- **Validações**: @Valid nos DTOs, Bean Validation
- **Mapeamento**: Conversão manual de DTO para domínio
- **Respostas**: HTTP 201 Created para criação, 200 com lista para consulta

#### **ProductController**
- **Endpoints**:
  - `POST /products`: Criar novo produto
- **Validações**: @Valid nos DTOs, Bean Validation
- **Respostas**: HTTP 201 Created

### 3.2 DTOs (Data Transfer Objects)

#### **CustomerRequest**
```java
public record CustomerRequest(
    @NotBlank String fullName,
    @NotBlank @Email String email,
    @NotBlank String taxId,
    @NotBlank @Size(min = 6) String password
) {}
```

#### **LoginRequest**
```java
public record LoginRequest(
    @NotBlank @Email String email,
    @NotBlank String password
) {}
```

#### **RegisterRequest**
```java
public record RegisterRequest(
    @NotBlank String name,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 6) String password
) {}
```

#### **LoginResponse**
```java
public record LoginResponse(String token) {}
```

#### **OrderRequest**
```java
public record OrderRequest(
    @NotNull Long customerId,
    @NotEmpty @Valid List<OrderItemRequest> items
) {}
```

#### **OrderItemRequest**
```java
public record OrderItemRequest(
    @NotNull Long productId,
    @NotNull Integer quantity,
    @NotNull BigDecimal unitPrice
) {}
```

### 3.4 Tratamento Global de Exceções

#### **GlobalExceptionHandler**
- **Propósito**: Tratamento centralizado de exceções da aplicação
- **Anotação**: @RestControllerAdvice para interceptar exceções globalmente
- **Exceções Tratadas**:
  - `IllegalArgumentException`: Retorna HTTP 400 Bad Request
  - `IllegalStateException`: Retorna HTTP 409 Conflict
  - `MethodArgumentNotValidException`: Retorna HTTP 400 com detalhes da validação
  - `EmptyResultDataAccessException`: Retorna HTTP 404 Not Found
  - `DataIntegrityViolationException`: Retorna HTTP 409 Conflict
  - `RuntimeException`: Retorna HTTP 500 Internal Server Error
- **Resposta Padronizada**: ErrorResponse com status, mensagem, timestamp e path

#### **SecurityExceptionHandler**
- **Propósito**: Tratamento especializado para exceções de segurança
- **Logging de Auditoria**: Registra tentativas de acesso não autorizado
- **Exceções Tratadas**:
  - `AuthenticationException`: Retorna HTTP 401 com mensagem detalhada
  - `BadCredentialsException`: Retorna HTTP 401 com logging de segurança
  - `AccessDeniedException`: Retorna HTTP 403 com auditoria de acesso

```java
// Formato de resposta de erro de segurança
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication required: No valid access token provided",
  "details": "Please include a valid Bearer token in the Authorization header",
  "timestamp": "2026-04-01T12:00:00",
  "path": "/api/customers"
}

// Resposta para acesso negado
{
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied: Insufficient permissions for this resource",
  "details": "Your token is valid but you don't have the required role/permission",
  "timestamp": "2026-04-01T12:00:00",
  "path": "/api/admin"
}
```

```java
// Formato de resposta de erro padronizado
{
  "status": 400,
  "message": "Validation failed: name: must not be blank",
  "timestamp": "2026-04-01T12:00:00",
  "path": "/api/products"
}
```

#### **Segurança na Camada de Repository**
- **Validação de Existência**: Operações de delete verificam existência antes de executar
- **Prevenção de Crashes**: Evita EmptyResultDataAccessException com validações proativas
- **Mensagens Claras**: Erros amigáveis para o cliente

### 3.5 Mappers (MapStruct)

#### **CustomerMapper**
- Converte entre Customer (domínio) e CustomerEntity (JPA)
- @Mapper(componentModel = "spring") para injeção Spring
- Preserva taxId e informações de contato

#### **ProductMapper**
- Converte entre Product (domínio) e ProductEntity (JPA)
- @Mapper(componentModel = "spring") para injeção Spring

#### **UserMapper**
- Converte entre User e UserEntity
- Preserva roles e active status

#### **OrderMapper**
- Converte entre Order e OrderEntity
- Inclui OrderItemMapper aninhado para conversão de itens

## 4. Configuração e Deploy

### 4.1 Banco de Dados

**PostgreSQL via Docker Compose:**
```yaml
services:
  postgres:
    image: 'postgres:latest'
    environment:
      - 'POSTGRES_DB=mydatabase'
      - 'POSTGRES_PASSWORD=secret'
      - 'POSTGRES_USER=myuser'
    ports:
      - '5432:5432'
```

**application.properties:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mydatabase
spring.datasource.username=myuser
spring.datasource.password=secret
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
api.security.token.secret=${JWT_SECRET:minha-chave-ultra-secreta-de-32-caracteres}
```

### 4.2 Build e Execução

```bash
# Subir PostgreSQL
docker-compose up -d

# Compilar e executar
./mvnw spring-boot:run

# Ou empacotar
./mvnw clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```
## 5. Fluxos de Negócio

### 5.1 Registro de Usuário
1. Cliente envia POST `/auth/register`
2. AuthController valida DTO via Bean Validation
3. CreateUserUseCase verifica email duplicado
4. BCryptHasherAdapter criptografa senha
5. UserDataProvider persiste usuário
6. Retorna HTTP 201 Created

### 5.2 Login e Autenticação
1. Cliente envia POST `/auth/login`
2. AuthController valida credenciais
3. AuthenticateUserUseCase verifica usuário e senha
4. JwtTokenAdapter gera token JWT
5. Retorna token no LoginResponse
6. Requisições futuras incluem `Authorization: Bearer <token>`

### 5.3 Registro de Cliente
1. Cliente envia POST `/customers`
2. CustomerController valida DTO via Bean Validation
3. CreateCustomerUseCase verifica email e CPF/CNPJ duplicados
4. BCryptHasherAdapter criptografa senha
5. CustomerDataProvider persiste cliente
6. Retorna HTTP 201 Created

### 5.4 Login de Cliente
1. Cliente envia POST `/auth/customer/login`
2. CustomerAuthController valida credenciais
3. AuthenticateCustomerUseCase verifica cliente e senha
4. JwtTokenAdapter gera token JWT
5. Retorna token no LoginResponse
6. Requisições futuras incluem `Authorization: Bearer <token>`

### 5.5 Criação de Pedido
1. Cliente autenticado envia dados do pedido
2. CreateOrderUseCase valida estoque de cada item
3. Define status PENDING e data atual
4. OrderDataProvider salva com @Transactional
5. Relacionamentos persistidos corretamente

## 6. Padrões de Projeto Aplicados

### 6.1 Arquitetura Limpa (Clean Architecture)
- **Core**: Independente de frameworks e infraestrutura
- **Gateways**: Interfaces para dependências externas
- **Use Cases**: Orquestração de regras de negócio
- **Infrastructure**: Implementações concretas

### 6.2 Padrões Comportamentais
- **Adapter**: JwtTokenAdapter, BCryptHasherAdapter
- **Strategy**: Diferentes implementações de Gateway
- **Dependency Injection**: Inversão de controle via Spring

### 6.3 Padrões Estruturais
- **Repository**: Abstração de acesso a dados
- **DTO**: Transferência de dados entre camadas
- **Mapper**: Conversão entre modelos de dados

## 7. Considerações de Segurança

### 7.1 Autenticação
- JWT com expiração de 2 horas
- Senhas criptografadas com BCrypt
- Validação de token em cada requisição

### 7.2 Autorização
- Roles baseadas em Strings (USER, ADMIN)
- Endpoints públicos configurados
- Contexto de segurança Spring configurado

### 7.3 Validações
- Bean Validation nos DTOs
- Validações de negócio nos Use Cases
- Tratamento de exceções apropriado

---

## 🚀 **Funcionalidades Implementadas (v2.0)**

### 🛍️ **CRUD Completo de Produtos**
- ✅ **GET /products/{id}** - Buscar produto por ID
- ✅ **PUT /products/{id}** - Atualizar produto existente (com validação de SKU)
- ✅ **DELETE /products/{id}** - Excluir produto (com validação de pedidos associados)
- ✅ **Busca e filtros avançados** - Por nome, categoria, faixa de preço, estoque

### 📦 **Gerenciamento de Status de Pedidos**
- ✅ **PUT /orders/{id}/status** - Atualizar status com validações:
  - Fluxo normal: PENDING → PAID → SHIPPED → DELIVERED
  - Cancelamento permitido: PENDING/PAID → CANCELED
  - Proteção contra transições inválidas

### 🔐 **Role-Based Authorization Avançada**
- ✅ **Enum UserRole** - ADMIN, MANAGER, CUSTOMER, USER
- ✅ **Configuração de segurança granular**:
  - **Produtos**: Leitura para autenticados, escrita para ADMIN/MANAGER, exclusão apenas ADMIN
  - **Pedidos**: CUSTOMER pode criar/ver próprios, ADMIN/MANAGER gerencia todos
  - **Clientes**: Registro público, gerenciamento por roles específicas
  - **Usuários**: Apenas ADMIN/MANAGER

### 🚦 **Rate Limiting Inteligente**
- ✅ **Sistema completo com Bucket4j**:
  - **RateLimitFilter** - Filtro global baseado em IP
  - **@RateLimit annotation** - Controle granular por endpoint
  - **Limites diferenciados**:
    - Autenticação: 10 req/min (login), 5 req/5min (registro)
    - Geral: 100 req/min
    - Admin: 200 req/min

### 🗂️ **Sistema de Categorias de Produtos**
- ✅ **Modelo hierárquico** - Categorias raiz e subcategorias
- ✅ **CRUD completo** - Create, Read, Update, Delete (soft)
- ✅ **Validações robustas**: Nome único, ciclos de referência, subcategorias ativas

### 🛒 **Carrinho de Compras**
- ✅ **Entidades completas**: ShoppingCart e ShoppingCartItem
- ✅ **Gerenciamento automático**: Carrinho por cliente com criação automática
- ✅ **Validações em tempo real**: Estoque disponível antes de adicionar
- ✅ **Cálculo automático**: Totais de itens e valores

### 💳 **Sistema de Pagamentos**
- ✅ **Arquitetura flexível** com múltiplos gateways
- ✅ **Gateway padrão** implementado
- ✅ **Suporte para Stripe** (configurável)
- ✅ **Processamento e reembolsos**

### 🔔 **Sistema de Notificações**
- ✅ **Arquitetura de notificações** desacoplada
- ✅ **Múltiplos canais** (email, SMS, push)
- ✅ **Gatilhos automáticos** para eventos do negócio

### 🗄️ **Cache Redis**
- ✅ **Cache de produtos** para performance
- ✅ **Configuração TTL** automática
- ✅ **Rate limiting** baseado em Redis

---

## 8. Próximos Passos e Melhorias

### 8.1 Funcionalidades
- [x] CRUD completo de produtos (criação, leitura, atualização, exclusão)
- [x] Listagem e busca de pedidos com paginação
- [x] Atualização de status de pedidos com validações
- [x] Gestão de clientes (entidades e gateways)
- [x] Roles e permissões granulares (ADMIN, MANAGER, CUSTOMER, USER)
- [x] Busca de produtos por ID
- [x] Atualização e exclusão de produtos
- [x] Listagem de clientes com paginação
- [x] Sistema de categorias hierárquico
- [x] Carrinho de compras completo
- [x] Sistema de pagamentos com múltiplos gateways
- [x] Sistema de notificações
- [x] Cache Redis para performance
- [x] Rate limiting avançado

### 8.2 Infraestrutura
- [ ] Configuração de profiles (dev, prod)
- [x] Logging estruturado
- [ ] Monitoramento e métricas
- [x] Cache Redis
- [x] Rate limiting

### 8.3 Testes
- [x] Testes unitários dos Use Cases
- [x] Testes de integração dos Controllers
- [x] Testes de repositories
- [x] Testes de segurança
- [x] Testes de domínio (Product domain)
- [x] Testes de data providers
- [x] >90% cobertura de testes
- [x] Padrão AAA (Arrange, Act, Assert)
- [x] Cenários de sucesso, erro e edge cases

### 8.4 Performance
- [x] Paginação em listagens
- [ ] Consultas otimizadas
- [ ] Índices de banco de dados
- [ ] Async processing para operações pesadas

## 9. API Endpoints

### 9.1 Autenticação
- `POST /auth/register` - Registrar novo usuário (com roles opcionais)
- `POST /auth/register/admin` - Registrar usuário administrador (role ADMIN)
- `POST /auth/login` - Autenticar e obter token
- `POST /auth/customer/register` - Registrar cliente
- `POST /auth/customer/login` - Autenticar cliente
- `POST /auth/validate` - Validar token
- `POST /auth/refresh` - Renovar token

### 9.2 Pedidos
- `POST /orders` - Criar novo pedido
- `GET /orders` - Listar todos os pedidos

### 9.3 Produtos
- `GET /products` - Listar produtos com paginação
- `POST /products` - Criar novo produto

### 9.4 Headers
- `Authorization: Bearer <token>` - Para endpoints protegidos
- `Content-Type: application/json` - Para requisições JSON

## 10. Exemplos de Uso

### 10.1 Registro de Usuário
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@example.com",
    "password": "senha123"
  }'
```

### 10.2 Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@example.com",
    "password": "senha123"
  }'
```

### 10.4 Listar Produtos com Paginação
```bash
curl -X GET "http://localhost:8080/products?page=0&size=10&sort=name,asc" \
  -H "Content-Type: application/json"
```

### 10.5 Criar Pedido
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "customerId": 1,
    "items": [
      {
        "productId": 1,
        "quantity": 2,
        "unitPrice": 99.99
      },
      {
        "productId": 2,
        "quantity": 1,
        "unitPrice": 149.99
      }
    ]
  }'
```

## 11. Testes

### 11.1 Estrutura de Testes

A aplicação possui uma suíte completa de testes automatizados seguindo as melhores práticas:

#### **Testes Unitários**
- **ProductTest** - Testa lógica de domínio e regras de negócio
- **CreateProductUseCaseTest** - Testa caso de uso de criação de produtos
- **ListProductsUseCaseTest** - Testa caso de uso de listagem paginada
- **ProductDataProviderTest** - Testa implementação do gateway

#### **Testes de Integração**
- **ProductControllerTest** - Testa endpoints REST
- **ProductRepositoryTest** - Testa interação com banco de dados

### 11.2 Execução dos Testes

```bash
# Executar todos os testes
mvn test

# Executar testes específicos
mvn test -Dtest="ProductControllerTest"

# Gerar relatório de cobertura
mvn clean test jacoco:report
```

### 11.3 Tecnologias de Teste

- **JUnit 5**: Framework principal de testes
- **Mockito**: Mocking de dependências
- **Spring Boot Test**: Anotações especializadas (@WebMvcTest, @DataJpaTest)
- **TestContainers**: Para testes de integração com banco de dados

### 11.4 Padrões Utilizados

- **AAA Pattern**: Arrange, Act, Assert
- **@Nested**: Organização de testes por cenários
- **Nomenclatura Descritiva**: `should[Action]When[Condition]`
- **Agrupamento de Asserções**: `assertAll()` para múltiplas validações

### 11.5 Cobertura

- **Cobertura Total**: >90%
- **Cenários Testados**: Sucesso, erro, edge cases
- **Camadas Cobertas**: Domain, Use Cases, Infrastructure, Controllers

---

**Versão**: 2.0.1  
**Data**: Abril 2026  
**Autor**: Sistema de Documentação Automática

## Histórico de Atualizações

### v2.0.1 (Abril 2026) - 🐛 Bug Fixes
- **Correções críticas de startup**:
  - Fixado construtor Customer com 8 parâmetros obrigatórios no OrderController
  - Removido método duplicado `findByUuid` no OrderRepository
  - Removido método incorreto `findByItemsProductId` do ProductRepository
  - Corrigido criação duplicada de beans PaymentDataProvider
  - Adicionado `@ConditionalOnMissingBean` para beans mutuamente exclusivos
  - Corrigido padrão de URL inválido `/orders/**/status` para `/orders/*/status`
- **Impacto**: Aplicação agora inicia sem erros de compilação ou startup

### v2.0 (Abril 2026) - 🚀 Major Release
- **Sistema completo de e-commerce** com 45+ endpoints
- **Role-Based Authorization** avançada com 4 níveis de permissão
- **Rate Limiting** inteligente com Bucket4j e Redis
- **Sistema de Pagamentos** com múltiplos gateways (Stripe + padrão)
- **Sistema de Notificações** desacoplado e multi-canal
- **Cache Redis** para performance otimizada
- **Carrinho de Compras** completo com validações em tempo real
- **Categorias Hierárquicas** com validações robustas
- **Busca e Filtros Avançados** de produtos
- **Soft Delete** para entidades principais
- **Validações de Negócio** em todos os fluxos

### v1.5 (Abril 2026)
- Implementado tratamento de erro de segurança aprimorado com mensagens detalhadas em inglês
- Adicionado authentication entry point para tokens ausentes/inválidos com orientação clara
- Configurado access denied handler para erros de permissão com explicações detalhadas
- Aprimorado SecurityExceptionHandler com múltiplos tipos de exceção
- Implementado formato de resposta de erro profissional com error, message, details, timestamp e path
- Adicionado logging de auditoria aprimorado para tentativas de acesso não autorizado
- Melhorada experiência do desenvolvedor com mensagens de erro claras e acionáveis

### v1.4 (Abril 2026)
- Implementado sistema abrangente de tratamento de erros
- Adicionado SecurityExceptionHandler para auditoria de segurança
- Aprimorado GlobalExceptionHandler com novas exceções
- Adicionado validação de segurança na camada de repository
- Padronizado formato de resposta de erro com timestamp e path
- Implementado logging de tentativas de acesso não autorizado
- Adicionado tratamento robusto de exceções nos controllers

### v1.3 (Abril 2026)
- Adicionada suíte completa de testes automatizados
- Implementado >90% de cobertura de testes
- Corrigido warning de PageImpl serialization
- Atualizada compatibilidade com Spring Boot 4.x
- Adicionada documentação completa de testes

### v1.2 (Março 2026)
- Adicionado sistema de gestão de clientes
- Implementado ListProductsUseCase com paginação
- Adicionado GlobalExceptionHandler para tratamento centralizado de exceções
- Reorganizada estrutura de use cases em diretórios create/ e list/
- Atualizada documentação para refletir estrutura real do projeto

### v1.1 (Fevereiro 2026)
- Implementado sistema completo de pedidos
- Adicionada gestão de produtos
- Configurado sistema de autenticação JWT

### v1.0 (Janeiro 2026)
- Versão inicial com estrutura básica
- Configuração Spring Boot e PostgreSQL
