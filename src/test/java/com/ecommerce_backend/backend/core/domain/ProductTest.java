package com.ecommerce_backend.backend.core.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product Domain Tests")
class ProductTest {

    @Nested
    @DisplayName("Stock Validation Tests")
    class StockValidationTests {

        @Test
        @DisplayName("Should return true when product has sufficient stock")
        void shouldReturnTrueWhenProductHasSufficientStock() {
            // Arrange
            Product product = new Product(1L, "Laptop", "LP001", new BigDecimal("5000.00"), 10);
            int requestedQuantity = 5;

            // Act
            boolean hasStock = product.hasStock(requestedQuantity);

            // Assert
            assertTrue(hasStock, "Product should have sufficient stock when stock >= requested quantity");
        }

        @Test
        @DisplayName("Should return true when product has exactly the requested stock")
        void shouldReturnTrueWhenProductHasExactlyRequestedStock() {
            // Arrange
            Product product = new Product(1L, "Mouse", "MS001", new BigDecimal("150.00"), 7);
            int requestedQuantity = 7;

            // Act
            boolean hasStock = product.hasStock(requestedQuantity);

            // Assert
            assertTrue(hasStock, "Product should have sufficient stock when stock == requested quantity");
        }

        @Test
        @DisplayName("Should return false when product has insufficient stock")
        void shouldReturnFalseWhenProductHasInsufficientStock() {
            // Arrange
            Product product = new Product(1L, "Keyboard", "KB001", new BigDecimal("200.00"), 3);
            int requestedQuantity = 5;

            // Act
            boolean hasStock = product.hasStock(requestedQuantity);

            // Assert
            assertFalse(hasStock, "Product should not have sufficient stock when stock < requested quantity");
        }

        @Test
        @DisplayName("Should return false when product has zero stock")
        void shouldReturnFalseWhenProductHasZeroStock() {
            // Arrange
            Product product = new Product(1L, "Monitor", "MN001", new BigDecimal("800.00"), 0);
            int requestedQuantity = 1;

            // Act
            boolean hasStock = product.hasStock(requestedQuantity);

            // Assert
            assertFalse(hasStock, "Product should not have sufficient stock when stock is zero");
        }
    }

    @Nested
    @DisplayName("Product Creation Tests")
    class ProductCreationTests {

        @Test
        @DisplayName("Should create product with all fields correctly")
        void shouldCreateProductWithAllFieldsCorrectly() {
            // Arrange
            Long expectedId = 1L;
            String expectedName = "Gaming Chair";
            String expectedSku = "GC001";
            BigDecimal expectedPrice = new BigDecimal("350.00");
            Integer expectedStock = 15;

            // Act
            Product product = new Product(expectedId, expectedName, expectedSku, expectedPrice, expectedStock);

            // Assert
            assertAll("Product fields should be correctly set",
                () -> assertEquals(expectedId, product.id(), "Product ID should match"),
                () -> assertEquals(expectedName, product.name(), "Product name should match"),
                () -> assertEquals(expectedSku, product.sku(), "Product SKU should match"),
                () -> assertEquals(expectedPrice, product.price(), "Product price should match"),
                () -> assertEquals(expectedStock, product.stock(), "Product stock should match")
            );
        }

        @Test
        @DisplayName("Should create product with null ID for new products")
        void shouldCreateProductWithNullIdForNewProducts() {
            // Arrange
            String name = "New Product";
            String sku = "NP001";
            BigDecimal price = new BigDecimal("100.00");
            Integer stock = 50;

            // Act
            Product product = new Product(null, name, sku, price, stock);

            // Assert
            assertNull(product.id(), "New product should have null ID");
            assertEquals(name, product.name(), "Product name should match");
            assertEquals(sku, product.sku(), "Product SKU should match");
            assertEquals(price, product.price(), "Product price should match");
            assertEquals(stock, product.stock(), "Product stock should match");
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle zero requested quantity correctly")
        void shouldHandleZeroRequestedQuantityCorrectly() {
            // Arrange
            Product product = new Product(1L, "USB Cable", "UC001", new BigDecimal("25.00"), 10);
            int requestedQuantity = 0;

            // Act
            boolean hasStock = product.hasStock(requestedQuantity);

            // Assert
            assertTrue(hasStock, "Product should have sufficient stock for zero quantity request");
        }

        @Test
        @DisplayName("Should handle negative stock values")
        void shouldHandleNegativeStockValues() {
            // Arrange
            Product product = new Product(1L, "Defective Item", "DI001", new BigDecimal("10.00"), -5);
            int requestedQuantity = 1;

            // Act
            boolean hasStock = product.hasStock(requestedQuantity);

            // Assert
            assertFalse(hasStock, "Product with negative stock should not have sufficient stock");
        }
    }
}
