package com.ecommerce_backend.backend.infrastructure.dataprovider;

import com.ecommerce_backend.backend.core.domain.Product;
import com.ecommerce_backend.backend.entrypoints.mapper.ProductMapper;
import com.ecommerce_backend.backend.infrastructure.dataprovider.repository.ProductRepository;
import com.ecommerce_backend.backend.infrastructure.persistence.entity.ProductEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductDataProvider Tests")
class ProductDataProviderTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductDataProvider productDataProvider;

    @Nested
    @DisplayName("Save Product Tests")
    class SaveProductTests {

        @Test
        @DisplayName("Should save product successfully")
        void shouldSaveProductSuccessfully() {
            // Arrange
            Product domainProduct = new Product(null, "Laptop", "LP001", new BigDecimal("5000.00"), 10);
            ProductEntity entityToSave = new ProductEntity(null, "Laptop", "LP001", new BigDecimal("5000.00"), 10);
            ProductEntity savedEntity = new ProductEntity(1L, "Laptop", "LP001", new BigDecimal("5000.00"), 10);
            Product expectedProduct = new Product(1L, "Laptop", "LP001", new BigDecimal("5000.00"), 10);

            when(mapper.toEntity(domainProduct)).thenReturn(entityToSave);
            when(repository.save(entityToSave)).thenReturn(savedEntity);
            when(mapper.toDomain(savedEntity)).thenReturn(expectedProduct);

            // Act
            Product result = productDataProvider.save(domainProduct);

            // Assert
            assertAll("Product should be saved correctly",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(1L, result.id(), "Product should have generated ID"),
                () -> assertEquals("Laptop", result.name(), "Product name should match"),
                () -> assertEquals("LP001", result.sku(), "Product SKU should match"),
                () -> assertEquals(new BigDecimal("5000.00"), result.price(), "Product price should match"),
                () -> assertEquals(10, result.stock(), "Product stock should match")
            );

            verify(mapper, times(1)).toEntity(domainProduct);
            verify(repository, times(1)).save(entityToSave);
            verify(mapper, times(1)).toDomain(savedEntity);
        }

        @Test
        @DisplayName("Should handle save with existing product ID")
        void shouldHandleSaveWithExistingProductId() {
            // Arrange
            Product domainProduct = new Product(1L, "Updated Laptop", "LP001", new BigDecimal("4500.00"), 8);
            ProductEntity entityToSave = new ProductEntity(1L, "Updated Laptop", "LP001", new BigDecimal("4500.00"), 8);
            ProductEntity savedEntity = new ProductEntity(1L, "Updated Laptop", "LP001", new BigDecimal("4500.00"), 8);
            Product expectedProduct = new Product(1L, "Updated Laptop", "LP001", new BigDecimal("4500.00"), 8);

            when(mapper.toEntity(domainProduct)).thenReturn(entityToSave);
            when(repository.save(entityToSave)).thenReturn(savedEntity);
            when(mapper.toDomain(savedEntity)).thenReturn(expectedProduct);

            // Act
            Product result = productDataProvider.save(domainProduct);

            // Assert
            assertEquals(1L, result.id(), "Product ID should be preserved");
            assertEquals("Updated Laptop", result.name(), "Product name should be updated");
            verify(repository, times(1)).save(entityToSave);
        }
    }

    @Nested
    @DisplayName("Find All Products Tests")
    class FindAllProductsTests {

        @Test
        @DisplayName("Should return paginated list of products")
        void shouldReturnPaginatedListOfProducts() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<ProductEntity> entities = List.of(
                new ProductEntity(1L, "Laptop", "LP001", new BigDecimal("5000.00"), 10),
                new ProductEntity(2L, "Mouse", "MS001", new BigDecimal("150.00"), 25)
            );
            Page<ProductEntity> entityPage = new PageImpl<>(entities, pageable, 2);
            
            List<Product> domainProducts = List.of(
                new Product(1L, "Laptop", "LP001", new BigDecimal("5000.00"), 10),
                new Product(2L, "Mouse", "MS001", new BigDecimal("150.00"), 25)
            );
            Page<Product> expectedPage = new PageImpl<>(domainProducts, pageable, 2);

            when(repository.findAll(pageable)).thenReturn(entityPage);
            when(mapper.toDomain(entities.get(0))).thenReturn(domainProducts.get(0));
            when(mapper.toDomain(entities.get(1))).thenReturn(domainProducts.get(1));

            // Act
            Page<Product> result = productDataProvider.findAll(pageable);

            // Assert
            assertAll("Paginated products should be returned correctly",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(2, result.getContent().size(), "Should return 2 products"),
                () -> assertEquals(2, result.getTotalElements(), "Total elements should be 2"),
                () -> assertEquals("Laptop", result.getContent().get(0).name(), "First product should be Laptop"),
                () -> assertEquals("Mouse", result.getContent().get(1).name(), "Second product should be Mouse")
            );

            verify(repository, times(1)).findAll(pageable);
            verify(mapper, times(2)).toDomain(any(ProductEntity.class));
        }

        @Test
        @DisplayName("Should return empty page when no products exist")
        void shouldReturnEmptyPageWhenNoProductsExist() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<ProductEntity> emptyEntityPage = new PageImpl<>(List.of(), pageable, 0);

            when(repository.findAll(pageable)).thenReturn(emptyEntityPage);

            // Act
            Page<Product> result = productDataProvider.findAll(pageable);

            // Assert
            assertAll("Empty page should be returned correctly",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertTrue(result.getContent().isEmpty(), "Content should be empty"),
                () -> assertEquals(0, result.getTotalElements(), "Total elements should be 0")
            );

            verify(repository, times(1)).findAll(pageable);
            verify(mapper, never()).toDomain(any(ProductEntity.class));
        }
    }

    @Nested
    @DisplayName("Find Product By SKU Tests")
    class FindProductBySkuTests {

        @Test
        @DisplayName("Should find product by SKU when it exists")
        void shouldFindProductBySkuWhenItExists() {
            // Arrange
            String sku = "LP001";
            ProductEntity entity = new ProductEntity(1L, "Laptop", sku, new BigDecimal("5000.00"), 10);
            Product expectedProduct = new Product(1L, "Laptop", sku, new BigDecimal("5000.00"), 10);

            when(repository.findBySku(sku)).thenReturn(Optional.of(entity));
            when(mapper.toDomain(entity)).thenReturn(expectedProduct);

            // Act
            Optional<Product> result = productDataProvider.findBySku(sku);

            // Assert
            assertTrue(result.isPresent(), "Result should be present");
            Product product = result.get();
            assertAll("Product should be found correctly",
                () -> assertEquals(1L, product.id(), "Product ID should match"),
                () -> assertEquals("Laptop", product.name(), "Product name should match"),
                () -> assertEquals(sku, product.sku(), "Product SKU should match"),
                () -> assertEquals(new BigDecimal("5000.00"), product.price(), "Product price should match"),
                () -> assertEquals(10, product.stock(), "Product stock should match")
            );

            verify(repository, times(1)).findBySku(sku);
            verify(mapper, times(1)).toDomain(entity);
        }

        @Test
        @DisplayName("Should return empty optional when product by SKU does not exist")
        void shouldReturnEmptyOptionalWhenProductBySkuDoesNotExist() {
            // Arrange
            String sku = "NONEXISTENT";

            when(repository.findBySku(sku)).thenReturn(Optional.empty());

            // Act
            Optional<Product> result = productDataProvider.findBySku(sku);

            // Assert
            assertFalse(result.isPresent(), "Result should be empty");
            verify(repository, times(1)).findBySku(sku);
            verify(mapper, never()).toDomain(any(ProductEntity.class));
        }

        @Test
        @DisplayName("Should handle null SKU gracefully")
        void shouldHandleNullSkuGracefully() {
            // Arrange
            when(repository.findBySku(null)).thenReturn(Optional.empty());

            // Act
            Optional<Product> result = productDataProvider.findBySku(null);

            // Assert
            assertFalse(result.isPresent(), "Result should be empty for null SKU");
            verify(repository, times(1)).findBySku(null);
            verify(mapper, never()).toDomain(any(ProductEntity.class));
        }
    }

    @Nested
    @DisplayName("Find Product By ID Tests")
    class FindProductByIdTests {

        @Test
        @DisplayName("Should find product by ID when it exists")
        void shouldFindProductByIdWhenItExists() {
            // Arrange
            Long id = 1L;
            ProductEntity entity = new ProductEntity(id, "Laptop", "LP001", new BigDecimal("5000.00"), 10);
            Product expectedProduct = new Product(id, "Laptop", "LP001", new BigDecimal("5000.00"), 10);

            when(repository.findById(id)).thenReturn(Optional.of(entity));
            when(mapper.toDomain(entity)).thenReturn(expectedProduct);

            // Act
            Optional<Product> result = productDataProvider.findById(id);

            // Assert
            assertTrue(result.isPresent(), "Result should be present");
            Product product = result.get();
            assertEquals(id, product.id(), "Product ID should match");
            assertEquals("Laptop", product.name(), "Product name should match");

            verify(repository, times(1)).findById(id);
            verify(mapper, times(1)).toDomain(entity);
        }

        @Test
        @DisplayName("Should return empty optional when product by ID does not exist")
        void shouldReturnEmptyOptionalWhenProductByIdDoesNotExist() {
            // Arrange
            Long id = 999L;

            when(repository.findById(id)).thenReturn(Optional.empty());

            // Act
            Optional<Product> result = productDataProvider.findById(id);

            // Assert
            assertFalse(result.isPresent(), "Result should be empty");
            verify(repository, times(1)).findById(id);
            verify(mapper, never()).toDomain(any(ProductEntity.class));
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle repository exceptions during save")
        void shouldHandleRepositoryExceptionsDuringSave() {
            // Arrange
            Product domainProduct = new Product(null, "Laptop", "LP001", new BigDecimal("5000.00"), 10);
            ProductEntity entity = new ProductEntity(null, "Laptop", "LP001", new BigDecimal("5000.00"), 10);

            when(mapper.toEntity(domainProduct)).thenReturn(entity);
            when(repository.save(entity)).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThrows(
                RuntimeException.class,
                () -> productDataProvider.save(domainProduct),
                "Should propagate runtime exceptions from repository"
            );

            verify(mapper, times(1)).toEntity(domainProduct);
            verify(repository, times(1)).save(entity);
            verify(mapper, never()).toDomain(any(ProductEntity.class));
        }

        @Test
        @DisplayName("Should handle mapper exceptions during save")
        void shouldHandleMapperExceptionsDuringSave() {
            // Arrange
            Product domainProduct = new Product(null, "Laptop", "LP001", new BigDecimal("5000.00"), 10);

            when(mapper.toEntity(domainProduct)).thenThrow(new RuntimeException("Mapping error"));

            // Act & Assert
            assertThrows(
                RuntimeException.class,
                () -> productDataProvider.save(domainProduct),
                "Should propagate runtime exceptions from mapper"
            );

            verify(mapper, times(1)).toEntity(domainProduct);
            verify(repository, never()).save(any(ProductEntity.class));
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should verify correct mapper calls for findAll")
        void shouldVerifyCorrectMapperCallsForFindAll() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<ProductEntity> entities = List.of(
                new ProductEntity(1L, "Product 1", "P001", new BigDecimal("100.00"), 5),
                new ProductEntity(2L, "Product 2", "P002", new BigDecimal("200.00"), 10)
            );
            Page<ProductEntity> entityPage = new PageImpl<>(entities, pageable, 2);

            when(repository.findAll(pageable)).thenReturn(entityPage);
            when(mapper.toDomain(any(ProductEntity.class)))
                .thenReturn(new Product(1L, "Product 1", "P001", new BigDecimal("100.00"), 5))
                .thenReturn(new Product(2L, "Product 2", "P002", new BigDecimal("200.00"), 10));

            // Act
            productDataProvider.findAll(pageable);

            // Assert
            verify(mapper, times(1)).toDomain(entities.get(0));
            verify(mapper, times(1)).toDomain(entities.get(1));
        }
    }
}
