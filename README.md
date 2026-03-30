# Documentação da Aplicação E-commerce Backend

## Visão Geral

Esta é uma aplicação backend de e-commerce desenvolvida com Spring Boot 4.0.4 e Java 21, seguindo uma arquitetura limpa com separação clara de responsabilidades. A aplicação utiliza PostgreSQL como banco de dados e implementa autenticação via JWT.

## Stack Tecnológico

- **Java 21**
- **Spring Boot 4.0.4**
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Segurança e autenticação
- **PostgreSQL** - Banco de dados relacional
- **Docker Compose** - Containerização do banco de dados
- **MapStruct 1.5.5** - Mapeamento entre entidades e domínios
- **JWT (Auth0)** - Tokens de autenticação
- **Lombok** - Redução de código boilerplate
- **Maven** - Gerenciamento de dependências

## Estrutura do Projeto

```
src/main/java/com/ecommerce_backend/backend/
├── BackendApplication.java          # Classe principal da aplicação
├── core/                           # Camada de Domínio (Core)
│   ├── domain/                     # Entidades de negócio
│   │   ├── Customer.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── OrderStatus.java
│   │   ├── Product.java
│   │   └── User.java
│   ├── gateway/                    # Interfaces de integração
│   │   ├── OrderGateway.java
│   │   ├── PasswordHasherGateway.java
│   │   ├── ProductGateway.java
│   │   ├── TokenServiceGateway.java
│   │   └── UserGateway.java
│   └── useCases/                   # Casos de uso (regras de negócio)
│       ├── AuthenticateUserUseCase.java
│       ├── CreateOrderUseCase.java
│       ├── CreateProductUseCase.java
│       └── CreateUserUseCase.java
├── entrypoints/                    # Camada de Apresentação
│   ├── controller/                 # Controllers REST
│   │   └── AuthController.java
│   ├── dto/                        # Data Transfer Objects
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   └── RegisterRequest.java
│   └── mapper/                     # Mapeamento de DTOs
│       ├── OrderMapper.java
│       ├── ProductMapper.java
│       └── UserMapper.java
└── infrastructure/                 # Camada de Infraestrutura
    ├── config/                     # Configurações Spring
    │   ├── OrderConfig.java
    │   ├── SecurityConfig.java
    │   └── UserConfig.java
    ├── dataprovider/               # Implementações dos Gateways
    │   ├── OrderDataProvider.java
    │   ├── ProductDataProvider.java
    │   └── UserDataProvider.java
    ├── persistence/                 # Entidades JPA
    │   └── entity/
    │       ├── OrderEntity.java
    │       ├── OrderItemEntity.java
    │       ├── ProductEntity.java
    │       └── UserEntity.java
    ├── repository/                 # Repositórios Spring Data
    │   ├── OrderRepository.java
    │   ├── ProductRepository.java
    │   └── UserRepository.java
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
    Long id,
    String fullName,
    String email,
    String taxId // CPF/CNPJ
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
- **UserGateway**: Operações com usuários (save, findByEmail)
- **OrderGateway**: Operações com pedidos (save)
- **PasswordHasherGateway**: Criptografia de senhas (hash, matches)
- **TokenServiceGateway**: Geração e validação de tokens JWT

### 1.3 Casos de Uso (Use Cases)

#### **CreateProductUseCase**
- **Responsabilidade**: Criar novos produtos
- **Validação**: Impede duplicação de SKU
- **Fluxo**: Verifica SKU → Salva produto

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

## 2. Camada de Infraestrutura

### 2.1 Configurações Spring

#### **SecurityConfig.java**
- **Propósito**: Configuração principal de segurança
- **Features**: Desabilita CSRF, sessão stateless, JWT filter
- **Endpoints Públicos**: `/auth/login`, `/auth/register`
- **Bean**: PasswordEncoder (BCrypt), AuthenticationManager

#### **UserConfig.java** & **OrderConfig.java**
- **Propósito**: Configuração de beans dos casos de uso
- **Implementação**: Injeção de dependências via @Bean

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

### 3.2 DTOs (Data Transfer Objects)

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

### 3.3 Mappers (MapStruct)

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

### 5.3 Criação de Pedido
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

## 8. Próximos Passos e Melhorias

### 8.1 Funcionalidades
- [ ] CRUD completo de produtos
- [ ] Listagem e busca de pedidos
- [ ] Atualização de status de pedidos
- [ ] Gestão de clientes
- [ ] Roles e permissões granulares

### 8.2 Infraestrutura
- [ ] Configuração de profiles (dev, prod)
- [ ] Logging estruturado
- [ ] Monitoramento e métricas
- [ ] Cache Redis
- [ ] Rate limiting

### 8.3 Testes
- [ ] Testes unitários dos Use Cases
- [ ] Testes de integração dos Controllers
- [ ] Testes de repositories
- [ ] Testes de segurança

### 8.4 Performance
- [ ] Paginação em listagens
- [ ] Consultas otimizadas
- [ ] Índices de banco de dados
- [ ] Async processing para operações pesadas

## 9. API Endpoints

### 9.1 Autenticação
- `POST /auth/register` - Registrar novo usuário
- `POST /auth/login` - Autenticar e obter token

### 9.2 Headers
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

---

**Versão**: 1.0  
**Data**: Março 2025  
**Autor**: Sistema de Documentação Automática
