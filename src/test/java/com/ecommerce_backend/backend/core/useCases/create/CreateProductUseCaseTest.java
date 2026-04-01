package com.ecommerce_backend.backend.core.useCases.create;

import com.ecommerce_backend.backend.core.domain.Product;
import com.ecommerce_backend.backend.core.gateway.ProductGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateProductUseCase Tests")
class CreateProductUseCaseTest {

    @Mock
    private ProductGateway productGateway;

    @InjectMocks
    private CreateProductUseCase createProductUseCase;

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessScenarios {

        @Test
        @DisplayName("Should create product successfully when SKU is unique")
        void shouldCreateProductSuccessfullyWhenSkuIsUnique() {
            // Arrange
            Product newProduct = new Product(null, "Laptop", "LP001", new BigDecimal("5000.00"), 10);
            Product savedProduct = new Product(1L, "Laptop", "LP001", new BigDecimal("5000.00"), 10);

            when(productGateway.findBySku("LP001")).thenReturn(Optional.empty());
            when(productGateway.save(any(Product.class))).thenReturn(savedProduct);

            // Act
            Product result = createProductUseCase.execute(newProduct);

            // Assert
            assertAll("Product creation should be successful",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(1L, result.id(), "Product should have generated ID"),
                () -> assertEquals("Laptop", result.name(), "Product name should match"),
                () -> assertEquals("LP001", result.sku(), "Product SKU should match"),
                () -> assertEquals(new BigDecimal("5000.00"), result.price(), "Product price should match"),
                () -> assertEquals(10, result.stock(), "Product stock should match")
            );

            verify(productGateway, times(1)).findBySku("LP001");
            verify(productGateway, times(1)).save(newProduct);
        }

        @Test
        @DisplayName("Should create product with zero stock")
        void shouldCreateProductWithZeroStock() {
            // Arrange
            Product newProduct = new Product(null, "Sample Product", "SP001", new BigDecimal("100.00"), 0);
            Product savedProduct = new Product(1L, "Sample Product", "SP001", new BigDecimal("100.00"), 0);

            when(productGateway.findBySku("SP001")).thenReturn(Optional.empty());
            when(productGateway.save(any(Product.class))).thenReturn(savedProduct);

            // Act
            Product result = createProductUseCase.execute(newProduct);

            // Assert
            assertEquals(0, result.stock(), "Product should be created with zero stock");
            verify(productGateway, times(1)).save(newProduct);
        }
    }

    @Nested
    @DisplayName("Error Scenarios")
    class ErrorScenarios {

        @Test
        @DisplayName("Should throw exception when product with same SKU already exists")
        void shouldThrowExceptionWhenProductWithSameSkuAlreadyExists() {
            // Arrange
            Product newProduct = new Product(null, "Laptop", "LP001", new BigDecimal("5000.00"), 10);
            Product existingProduct = new Product(1L, "Old Laptop", "LP001", new BigDecimal("4500.00"), 5);

            when(productGateway.findBySku("LP001")).thenReturn(Optional.of(existingProduct));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createProductUseCase.execute(newProduct),
                "Should throw IllegalArgumentException for duplicate SKU"
            );

            assertEquals("Product with SKU LP001 already exists", exception.getMessage(),
                "Exception message should indicate duplicate SKU");

            verify(productGateway, times(1)).findBySku("LP001");
            verify(productGateway, never()).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw exception when SKU is null")
        void shouldThrowExceptionWhenSkuIsNull() {
            // Arrange
            Product newProduct = new Product(null, "Product", null, new BigDecimal("100.00"), 10);

            when(productGateway.findBySku(null)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(
                IllegalArgumentException.class,
                () -> createProductUseCase.execute(newProduct),
                "Should handle null SKU gracefully"
            );

            verify(productGateway, times(1)).findBySku(null);
            verify(productGateway, never()).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw exception when gateway save fails")
        void shouldThrowExceptionWhenGatewaySaveFails() {
            // Arrange
            Product newProduct = new Product(null, "Laptop", "LP001", new BigDecimal("5000.00"), 10);

            when(productGateway.findBySku("LP001")).thenReturn(Optional.empty());
            when(productGateway.save(any(Product.class))).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThrows(
                RuntimeException.class,
                () -> createProductUseCase.execute(newProduct),
                "Should propagate runtime exceptions from gateway"
            );

            verify(productGateway, times(1)).findBySku("LP001");
            verify(productGateway, times(1)).save(newProduct);
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should verify correct method call order")
        void shouldVerifyCorrectMethodCallOrder() {
            // Arrange
            Product newProduct = new Product(null, "Test Product", "TP001", new BigDecimal("200.00"), 5);
            Product savedProduct = new Product(1L, "Test Product", "TP001", new BigDecimal("200.00"), 5);

            when(productGateway.findBySku("TP001")).thenReturn(Optional.empty());
            when(productGateway.save(any(Product.class))).thenReturn(savedProduct);

            // Act
            createProductUseCase.execute(newProduct);

            // Assert - Verify call order
            var inOrder = inOrder(productGateway);
            inOrder.verify(productGateway).findBySku("TP001");
            inOrder.verify(productGateway).save(newProduct);
        }

        @Test
        @DisplayName("Should pass exact product to gateway save")
        void shouldPassExactProductToGatewaySave() {
            // Arrange
            Product newProduct = new Product(null, "Exact Product", "EP001", new BigDecimal("300.00"), 8);
            Product savedProduct = new Product(1L, "Exact Product", "EP001", new BigDecimal("300.00"), 8);

            when(productGateway.findBySku("EP001")).thenReturn(Optional.empty());
            when(productGateway.save(eq(newProduct))).thenReturn(savedProduct);

            // Act
            Product result = createProductUseCase.execute(newProduct);

            // Assert
            assertNotNull(result, "Should return saved product");
            verify(productGateway, times(1)).save(eq(newProduct));
        }
    }
}
