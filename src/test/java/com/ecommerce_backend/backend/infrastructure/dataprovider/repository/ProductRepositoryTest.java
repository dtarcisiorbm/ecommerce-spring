package com.ecommerce_backend.backend.infrastructure.dataprovider.repository;

import com.ecommerce_backend.backend.infrastructure.persistence.entity.ProductEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("ProductRepository Integration Tests")
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Nested
    @DisplayName("Save Product Tests")
    class SaveProductTests {

        @Test
        @DisplayName("Should save product successfully")
        void shouldSaveProductSuccessfully() {
            // Arrange
            ProductEntity product = ProductEntity.builder()
                .name("Test Product")
                .sku("TP001")
                .price(new BigDecimal("100.00"))
                .stock(10)
                .build();

            // Act
            ProductEntity savedProduct = productRepository.save(product);

            // Assert
            assertAll("Product should be saved correctly",
                () -> assertNotNull(savedProduct.getId(), "ID should be generated"),
                () -> assertEquals("Test Product", savedProduct.getName(), "Name should match"),
                () -> assertEquals("TP001", savedProduct.getSku(), "SKU should match"),
                () -> assertEquals(new BigDecimal("100.00"), savedProduct.getPrice(), "Price should match"),
                () -> assertEquals(10, savedProduct.getStock(), "Stock should match")
            );

            // Verify database persistence
            ProductEntity foundProduct = entityManager.find(ProductEntity.class, savedProduct.getId());
            assertNotNull(foundProduct, "Product should be found in database");
            assertEquals(savedProduct.getSku(), foundProduct.getSku(), "SKU should be persisted correctly");
        }

        @Test
        @DisplayName("Should update existing product")
        void shouldUpdateExistingProduct() {
            // Arrange
            ProductEntity product = ProductEntity.builder()
                .name("Original Product")
                .sku("OP001")
                .price(new BigDecimal("50.00"))
                .stock(5)
                .build();
            
            ProductEntity savedProduct = productRepository.save(product);
            Long productId = savedProduct.getId();

            // Act
            savedProduct.setName("Updated Product");
            savedProduct.setPrice(new BigDecimal("75.00"));
            savedProduct.setStock(8);
            ProductEntity updatedProduct = productRepository.save(savedProduct);

            // Assert
            assertAll("Product should be updated correctly",
                () -> assertEquals(productId, updatedProduct.getId(), "ID should remain the same"),
                () -> assertEquals("Updated Product", updatedProduct.getName(), "Name should be updated"),
                () -> assertEquals("OP001", updatedProduct.getSku(), "SKU should remain the same"),
                () -> assertEquals(new BigDecimal("75.00"), updatedProduct.getPrice(), "Price should be updated"),
                () -> assertEquals(8, updatedProduct.getStock(), "Stock should be updated")
            );
        }
    }

    @Nested
    @DisplayName("Find By SKU Tests")
    class FindBySkuTests {

        @Test
        @DisplayName("Should find product by SKU when it exists")
        void shouldFindProductBySkuWhenItExists() {
            // Arrange
            ProductEntity product = ProductEntity.builder()
                .name("Findable Product")
                .sku("FP001")
                .price(new BigDecimal("200.00"))
                .stock(15)
                .build();
            
            entityManager.persistAndFlush(product);

            // Act
            Optional<ProductEntity> result = productRepository.findBySku("FP001");

            // Assert
            assertTrue(result.isPresent(), "Product should be found");
            ProductEntity foundProduct = result.get();
            assertAll("Found product should match",
                () -> assertEquals("Findable Product", foundProduct.getName()),
                () -> assertEquals("FP001", foundProduct.getSku()),
                () -> assertEquals(new BigDecimal("200.00"), foundProduct.getPrice()),
                () -> assertEquals(15, foundProduct.getStock())
            );
        }

        @Test
        @DisplayName("Should return empty when product by SKU does not exist")
        void shouldReturnEmptyWhenProductBySkuDoesNotExist() {
            // Act
            Optional<ProductEntity> result = productRepository.findBySku("NONEXISTENT");

            // Assert
            assertFalse(result.isPresent(), "Should return empty optional");
        }

        @Test
        @DisplayName("Should be case sensitive for SKU search")
        void shouldBeCaseSensitiveForSkuSearch() {
            // Arrange
            ProductEntity product = ProductEntity.builder()
                .name("Case Product")
                .sku("CP001")
                .price(new BigDecimal("100.00"))
                .stock(10)
                .build();
            
            entityManager.persistAndFlush(product);

            // Act & Assert
            assertTrue(productRepository.findBySku("CP001").isPresent(), "Should find exact case");
            assertFalse(productRepository.findBySku("cp001").isPresent(), "Should not find different case");
            assertFalse(productRepository.findBySku("Cp001").isPresent(), "Should not find mixed case");
        }
    }

    @Nested
    @DisplayName("Find By ID Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should find product by ID when it exists")
        void shouldFindProductByIdWhenItExists() {
            // Arrange
            ProductEntity product = ProductEntity.builder()
                .name("ID Product")
                .sku("ID001")
                .price(new BigDecimal("300.00"))
                .stock(20)
                .build();
            
            ProductEntity savedProduct = entityManager.persistAndFlush(product);

            // Act
            Optional<ProductEntity> result = productRepository.findById(savedProduct.getId());

            // Assert
            assertTrue(result.isPresent(), "Product should be found");
            ProductEntity foundProduct = result.get();
            assertEquals(savedProduct.getId(), foundProduct.getId(), "ID should match");
            assertEquals("ID Product", foundProduct.getName(), "Name should match");
        }

        @Test
        @DisplayName("Should return empty when product by ID does not exist")
        void shouldReturnEmptyWhenProductByIdDoesNotExist() {
            // Act
            Optional<ProductEntity> result = productRepository.findById(999999L);

            // Assert
            assertFalse(result.isPresent(), "Should return empty optional for non-existent ID");
        }
    }

    @Nested
    @DisplayName("Find All Tests")
    class FindAllTests {

        @Test
        @DisplayName("Should find all products with pagination")
        void shouldFindAllProductsWitPagination() {
            // Arrange
            for (int i = 1; i <= 25; i++) {
                ProductEntity product = ProductEntity.builder()
                    .name("Product " + i)
                    .sku("P" + String.format("%03d", i))
                    .price(new BigDecimal(String.valueOf(i * 10)))
                    .stock(i)
                    .build();
                entityManager.persistAndFlush(product);
            }

            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<ProductEntity> result = productRepository.findAll(pageable);

            // Assert
            assertAll("Pagination should work correctly",
                () -> assertEquals(25, result.getTotalElements(), "Total elements should be 25"),
                () -> assertEquals(3, result.getTotalPages(), "Should have 3 pages"),
                () -> assertEquals(10, result.getContent().size(), "First page should have 10 items"),
                () -> assertEquals(0, result.getNumber(), "Should be page 0"),
                () -> assertTrue(result.hasNext(), "Should have next page"),
                () -> assertFalse(result.hasPrevious(), "Should not have previous page")
            );
        }

        @Test
        @DisplayName("Should return empty page when no products exist")
        void shouldReturnEmptyPageWhenNoProductsExist() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);

            // Act
            Page<ProductEntity> result = productRepository.findAll(pageable);

            // Assert
            assertAll("Empty page should be returned",
                () -> assertEquals(0, result.getTotalElements(), "Total elements should be 0"),
                () -> assertEquals(0, result.getTotalPages(), "Total pages should be 0"),
                () -> assertTrue(result.getContent().isEmpty(), "Content should be empty")
            );
        }

        @Test
        @DisplayName("Should handle pagination with sorting")
        void shouldHandlePaginationWithSorting() {
            // Arrange
            String[] names = {"Zebra", "Apple", "Banana", "Cherry"};
            for (int i = 0; i < names.length; i++) {
                ProductEntity product = ProductEntity.builder()
                    .name(names[i])
                    .sku("S" + String.format("%03d", i))
                    .price(new BigDecimal("100.00"))
                    .stock(10)
                    .build();
                entityManager.persistAndFlush(product);
            }

            Pageable pageable = PageRequest.of(0, 10, 
                org.springframework.data.domain.Sort.by("name").ascending());

            // Act
            Page<ProductEntity> result = productRepository.findAll(pageable);

            // Assert
            assertEquals(4, result.getContent().size(), "Should return 4 products");
            assertEquals("Apple", result.getContent().get(0).getName(), "First should be Apple");
            assertEquals("Banana", result.getContent().get(1).getName(), "Second should be Banana");
            assertEquals("Cherry", result.getContent().get(2).getName(), "Third should be Cherry");
            assertEquals("Zebra", result.getContent().get(3).getName(), "Fourth should be Zebra");
        }
    }

    @Nested
    @DisplayName("Constraint Tests")
    class ConstraintTests {

        @Test
        @DisplayName("Should enforce unique SKU constraint")
        void shouldEnforceUniqueSkuConstraint() {
            // Arrange
            ProductEntity product1 = ProductEntity.builder()
                .name("First Product")
                .sku("UNIQUE001")
                .price(new BigDecimal("100.00"))
                .stock(10)
                .build();
            
            entityManager.persistAndFlush(product1);

            ProductEntity product2 = ProductEntity.builder()
                .name("Second Product")
                .sku("UNIQUE001") // Same SKU
                .price(new BigDecimal("200.00"))
                .stock(20)
                .build();

            // Act & Assert
            assertThrows(DataIntegrityViolationException.class, 
                () -> productRepository.saveAndFlush(product2),
                "Should throw exception for duplicate SKU");
        }

        @Test
        @DisplayName("Should enforce not null constraints")
        void shouldEnforceNotNullConstraints() {
            // Act & Assert - Test null name
            ProductEntity productWithNullName = ProductEntity.builder()
                .name(null)
                .sku("NULL_NAME")
                .price(new BigDecimal("100.00"))
                .stock(10)
                .build();

            assertThrows(DataIntegrityViolationException.class,
                () -> productRepository.saveAndFlush(productWithNullName),
                "Should throw exception for null name");

            // Act & Assert - Test null SKU
            ProductEntity productWithNullSku = ProductEntity.builder()
                .name("Null SKU Product")
                .sku(null)
                .price(new BigDecimal("100.00"))
                .stock(10)
                .build();

            assertThrows(DataIntegrityViolationException.class,
                () -> productRepository.saveAndFlush(productWithNullSku),
                "Should throw exception for null SKU");

            // Act & Assert - Test null price
            ProductEntity productWithNullPrice = ProductEntity.builder()
                .name("Null Price Product")
                .sku("NULL_PRICE")
                .price(null)
                .stock(10)
                .build();

            assertThrows(DataIntegrityViolationException.class,
                () -> productRepository.saveAndFlush(productWithNullPrice),
                "Should throw exception for null price");

            // Act & Assert - Test null stock
            ProductEntity productWithNullStock = ProductEntity.builder()
                .name("Null Stock Product")
                .sku("NULL_STOCK")
                .price(new BigDecimal("100.00"))
                .stock(null)
                .build();

            assertThrows(DataIntegrityViolationException.class,
                () -> productRepository.saveAndFlush(productWithNullStock),
                "Should throw exception for null stock");
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Should handle large dataset efficiently")
        void shouldHandleLargeDatasetEfficiently() {
            // Arrange
            long startTime = System.currentTimeMillis();
            
            for (int i = 1; i <= 1000; i++) {
                ProductEntity product = ProductEntity.builder()
                    .name("Performance Product " + i)
                    .sku("PERF" + String.format("%04d", i))
                    .price(new BigDecimal(String.valueOf(i)))
                    .stock(i % 100)
                    .build();
                entityManager.persistAndFlush(product);
            }

            long insertTime = System.currentTimeMillis() - startTime;
            
            // Act
            startTime = System.currentTimeMillis();
            Pageable pageable = PageRequest.of(0, 50);
            Page<ProductEntity> result = productRepository.findAll(pageable);
            long queryTime = System.currentTimeMillis() - startTime;

            // Assert
            assertEquals(1000, result.getTotalElements(), "Should find all 1000 products");
            assertEquals(50, result.getContent().size(), "Should return 50 items per page");
            
            // Performance assertions (adjust thresholds based on your environment)
            assertTrue(insertTime < 10000, "Insert operations should complete within 10 seconds");
            assertTrue(queryTime < 1000, "Query should complete within 1 second");
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle zero and negative values correctly")
        void shouldHandleZeroAndNegativeValuesCorrectly() {
            // Arrange
            ProductEntity product = ProductEntity.builder()
                .name("Edge Case Product")
                .sku("EDGE001")
                .price(BigDecimal.ZERO)
                .stock(0)
                .build();

            // Act
            ProductEntity savedProduct = productRepository.save(product);

            // Assert
            assertAll("Zero values should be handled correctly",
                () -> assertEquals(BigDecimal.ZERO, savedProduct.getPrice(), "Zero price should be saved"),
                () -> assertEquals(0, savedProduct.getStock(), "Zero stock should be saved")
            );

            // Test negative values (if business logic allows)
            ProductEntity negativeProduct = ProductEntity.builder()
                .name("Negative Product")
                .sku("NEG001")
                .price(new BigDecimal("-100.00"))
                .stock(-5)
                .build();

            ProductEntity savedNegativeProduct = productRepository.save(negativeProduct);
            assertAll("Negative values should be handled correctly",
                () -> assertEquals(new BigDecimal("-100.00"), savedNegativeProduct.getPrice()),
                () -> assertEquals(-5, savedNegativeProduct.getStock())
            );
        }

        @Test
        @DisplayName("Should handle very long strings")
        void shouldHandleVeryLongStrings() {
            // Arrange
            String longName = "A".repeat(255); // Maximum length for typical VARCHAR
            String longSku = "B".repeat(50);   // Reasonable length for SKU

            ProductEntity product = ProductEntity.builder()
                .name(longName)
                .sku(longSku)
                .price(new BigDecimal("999.99"))
                .stock(100)
                .build();

            // Act
            ProductEntity savedProduct = productRepository.save(product);

            // Assert
            assertEquals(longName, savedProduct.getName(), "Long name should be preserved");
            assertEquals(longSku, savedProduct.getSku(), "Long SKU should be preserved");
        }
    }
}
