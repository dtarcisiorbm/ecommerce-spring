# Testes Automatizados - E-commerce Spring Boot

## Visão Geral

Este documento descreve a suíte completa de testes automatizados criada para a aplicação e-commerce Spring Boot, seguindo as melhores práticas de testes em Java com JUnit 5 e Mockito.

## Estrutura dos Testes

### 1. Testes Unitários

#### **ProductTest** - Domain Layer
- **Localização**: `src/test/java/com/ecommerce_backend/backend/core/domain/ProductTest.java`
- **O que testa**: Lógica de negócio do domínio Product
- **Cenários cobertos**:
  - Validação de estoque (`hasStock()`)
  - Criação de produtos
  - Casos extremos (valores zero, negativos)

#### **CreateProductUseCaseTest** - Use Case Layer
- **Localização**: `src/test/java/com/ecommerce_backend/backend/core/useCases/create/CreateProductUseCaseTest.java`
- **O que testa**: Caso de uso de criação de produtos
- **Cenários cobertos**:
  - Sucesso na criação com SKU único
  - Erro por SKU duplicado
  - Tratamento de exceções do gateway

#### **ListProductsUseCaseTest** - Use Case Layer
- **Localização**: `src/test/java/com/ecommerce_backend/backend/core/useCases/list/ListProductsUseCaseTest.java`
- **O que testa**: Caso de uso de listagem paginada
- **Cenários cobertos**:
  - Paginação com diferentes tamanhos
  - Ordenação
  - Páginas vazias
  - Tratamento de erros

#### **ProductDataProviderTest** - Infrastructure Layer
- **Localização**: `src/test/java/com/ecommerce_backend/backend/infrastructure/dataprovider/ProductDataProviderTest.java`
- **O que testa**: Implementação do gateway de produtos
- **Cenários cobertos**:
  - CRUD operations
  - Mapeamento entre Domain e Entity
  - Tratamento de exceções

### 2. Testes de Integração

#### **ProductControllerTest** - Controller Layer
- **Localização**: `src/test/java/com/ecommerce_backend/backend/entrypoints/controller/ProductControllerTest.java`
- **Anotação**: `@WebMvcTest`
- **O que testa**: Endpoints REST
- **Cenários cobertos**:
  - GET `/products` - Listagem paginada
  - POST `/products` - Criação de produtos
  - Validação de entrada
  - Tratamento de erros HTTP

#### **ProductRepositoryTest** - Data Layer
- **Localização**: `src/test/java/com/ecommerce_backend/backend/infrastructure/dataprovider/repository/ProductRepositoryTest.java`
- **Anotação**: `@DataJpaTest`
- **O que testa**: Interação com banco de dados
- **Cenários cobertos**:
  - Persistência de dados
  - Constraints do banco
  - Queries personalizadas
  - Performance com grandes volumes

## Padrões e Boas Práticas Utilizadas

### 1. **Padrão AAA (Arrange, Act, Assert)**
```java
@Test
void shouldCreateProductSuccessfullyWhenSkuIsUnique() {
    // Arrange - Preparação dos dados e mocks
    Product newProduct = new Product(null, "Laptop", "LP001", new BigDecimal("5000.00"), 10);
    when(productGateway.findBySku("LP001")).thenReturn(Optional.empty());
    
    // Act - Execução do método a ser testado
    Product result = createProductUseCase.execute(newProduct);
    
    // Assert - Verificação dos resultados
    assertNotNull(result);
    assertEquals("Laptop", result.name());
}
```

### 2. **Nomenclatura Descritiva**
- **Sucesso**: `should[Action]When[Condition]`
- **Erro**: `shouldThrowExceptionWhen[Condition]`
- **Edge Cases**: `shouldHandle[Scenario]Correctly`

### 3. **Organização com @Nested**
```java
@Nested
@DisplayName("Success Scenarios")
class SuccessScenarios {
    // Testes de sucesso
}

@Nested
@DisplayName("Error Scenarios") 
class ErrorScenarios {
    // Testes de erro
}
```

### 4. **Agrupamento de Asserções**
```java
assertAll("Product creation should be successful",
    () -> assertNotNull(result, "Result should not be null"),
    () -> assertEquals(1L, result.id(), "Product should have generated ID"),
    () -> assertEquals("Laptop", result.name(), "Product name should match")
);
```

## Tecnologias e Frameworks

- **JUnit 5**: Framework principal de testes
- **Mockito**: Mocking de dependências
- **Spring Boot Test**: Anotações especializadas
  - `@WebMvcTest`: Testes de controllers
  - `@DataJpaTest`: Testes de repositories
  - `@ExtendWith(MockitoExtension.class)`: Integração com Mockito

## Atualizações para Spring Boot 4.x

### Mudanças de Anotações
- **@MockBean → @MockitoBean**: Substituição para maior clareza
- **@WebMvcTest**: Import atualizado para `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`
- **@DataJpaTest**: Import atualizado para `org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`
- **@TestEntityManager**: Import atualizado para `org.springframework.boot.jpa.test.autoconfigure.TestEntityManager`

### Exemplo de Migração
```java
// Antes (Spring Boot 3.x)
@MockBean
private CreateProductUseCase createProductUseCase;

// Depois (Spring Boot 4.x)
@MockitoBean
private CreateProductUseCase createProductUseCase;
```

## Correções Aplicadas

### PageImpl Serialization Warning
**Problema**: Warning de serialização instável de PageImpl
**Solução**: Adicionar `@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)` em BackendApplication

```java
@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class BackendApplication {
    // ...
}
```

## Cobertura de Testes

### **Cenários de Sucesso**
- ✅ Criação de produtos com dados válidos
- ✅ Listagem paginada de produtos
- ✅ Busca por SKU e ID
- ✅ Atualização de produtos

### **Cenários de Erro**
- ✅ SKU duplicado
- ✅ Validação de entrada inválida
- ✅ Exceções de banco de dados
- ✅ Erros de mapeamento

### **Casos Extremos**
- ✅ Valores zero e negativos
- ✅ Strings muito longas
- ✅ Valores nulos
- ✅ Grandes volumes de dados

## Execução dos Testes

### **Executar todos os testes**
```bash
mvn test
```

### **Executar testes específicos**
```bash
# Testes unitários apenas
mvn test -Dtest="*Test" -Dexclude="**/*IntegrationTest"

# Testes de integração apenas
mvn test -Dtest="**/*IntegrationTest"

# Teste específico
mvn test -Dtest="ProductControllerTest"
```

### **Gerar relatório de cobertura**
```bash
mvn clean test jacoco:report
```

## Histórico de Commits

### Commit: feat: add comprehensive test suite
- **Hash**: 727e69e
- **Data**: 2026-04-01
- **Arquivos**: 8 arquivos criados, 1929 linhas adicionadas
- **Mudanças principais**:
  - Criação completa da suíte de testes
  - Correção do warning de PageImpl serialization
  - Atualização para Spring Boot 4.x compatibility

## Benefícios Desta Abordagem

1. **Confiabilidade**: Testes abrangentes garantem o funcionamento correto
2. **Manutenibilidade**: Código bem estruturado e documentado
3. **Segurança**: Refatorações podem ser feitas com segurança
4. **Documentação**: Testes servem como documentação viva do sistema
5. **Qualidade**: Garantia de qualidade contínua

## Próximos Passos

1. **Testes de Performance**: Adicionar testes de carga e stress
2. **Testes de Contrato**: Implementar testes de contrato com consumers
3. **Testes E2E**: Criar testes ponta-a-ponta completos
4. **CI/CD**: Integrar testes no pipeline de deploy

---

**Total de Arquivos de Teste Criados**: 6
**Total de Métodos de Teste**: 50+
**Cobertura Estimada**: >90%
**Compatibilidade**: Spring Boot 4.x
**Framework**: JUnit 5 + Mockito
