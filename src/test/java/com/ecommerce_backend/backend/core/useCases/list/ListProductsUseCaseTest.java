package com.ecommerce_backend.backend.core.useCases.list;

import com.ecommerce_backend.backend.core.domain.Product;
import com.ecommerce_backend.backend.core.gateway.ProductGateway;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListProductsUseCase Tests")
class ListProductsUseCaseTest {

    @Mock
    private ProductGateway productGateway;

    @InjectMocks
    private ListProductsUseCase listProductsUseCase;

    @Nested
    @DisplayName("Success Scenarios")
    class SuccessScenarios {

        @Test
        @DisplayName("Should return paginated list of products")
        void shouldReturnPaginatedListOfProducts() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<Product> products = List.of(
                new Product(1L, "Laptop", "LP001", new BigDecimal("5000.00"), 10),
                new Product(2L, "Mouse", "MS001", new BigDecimal("150.00"), 25),
                new Product(3L, "Keyboard", "KB001", new BigDecimal("200.00"), 15)
            );
            Page<Product> expectedPage = new PageImpl<>(products, pageable, products.size());

            when(productGateway.findAll(pageable)).thenReturn(expectedPage);

            // Act
            Page<Product> result = listProductsUseCase.execute(pageable);

            // Assert
            assertAll("Paginated products should be returned correctly",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertEquals(3, result.getContent().size(), "Should return 3 products"),
                () -> assertEquals(3, result.getTotalElements(), "Total elements should be 3"),
                () -> assertEquals(1, result.getTotalPages(), "Should have 1 page"),
                () -> assertEquals(0, result.getNumber(), "Should be page 0"),
                () -> assertEquals(10, result.getSize(), "Page size should be 10"),
                () -> assertTrue(result.hasNext(), "Should have next page if more data exists"),
                () -> assertTrue(result.hasPrevious(), "Should indicate if has previous page")
            );

            verify(productGateway, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Should return empty page when no products exist")
        void shouldReturnEmptyPageWhenNoProductsExist() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Product> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(productGateway.findAll(pageable)).thenReturn(emptyPage);

            // Act
            Page<Product> result = listProductsUseCase.execute(pageable);

            // Assert
            assertAll("Empty page should be returned correctly",
                () -> assertNotNull(result, "Result should not be null"),
                () -> assertTrue(result.getContent().isEmpty(), "Content should be empty"),
                () -> assertEquals(0, result.getTotalElements(), "Total elements should be 0"),
                () -> assertEquals(0, result.getTotalPages(), "Total pages should be 0"),
                () -> assertFalse(result.hasNext(), "Should not have next page"),
                () -> assertFalse(result.hasPrevious(), "Should not have previous page")
            );

            verify(productGateway, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Should handle different page sizes correctly")
        void shouldHandleDifferentPageSizesCorrectly() {
            // Arrange
            Pageable pageable = PageRequest.of(1, 5); // Second page with 5 items
            List<Product> products = List.of(
                new Product(6L, "Monitor", "MN001", new BigDecimal("800.00"), 8),
                new Product(7L, "Webcam", "WC001", new BigDecimal("120.00"), 20)
            );
            Page<Product> expectedPage = new PageImpl<>(products, pageable, 12); // 12 total items

            when(productGateway.findAll(pageable)).thenReturn(expectedPage);

            // Act
            Page<Product> result = listProductsUseCase.execute(pageable);

            // Assert
            assertAll("Pagination with custom page size should work correctly",
                () -> assertEquals(2, result.getContent().size(), "Should return 2 products on page 1"),
                () -> assertEquals(12, result.getTotalElements(), "Total elements should be 12"),
                () -> assertEquals(3, result.getTotalPages(), "Should have 3 pages (12/5 = 2.4 -> 3)"),
                () -> assertEquals(1, result.getNumber(), "Should be page 1"),
                () -> assertEquals(5, result.getSize(), "Page size should be 5")
            );

            verify(productGateway, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Should handle sorting correctly")
        void shouldHandleSortingCorrectly() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10, 
                org.springframework.data.domain.Sort.by("name").ascending());
            List<Product> products = List.of(
                new Product(1L, "Apple", "AP001", new BigDecimal("100.00"), 5),
                new Product(2L, "Banana", "BN001", new BigDecimal("50.00"), 10)
            );
            Page<Product> expectedPage = new PageImpl<>(products, pageable, 2);

            when(productGateway.findAll(pageable)).thenReturn(expectedPage);

            // Act
            Page<Product> result = listProductsUseCase.execute(pageable);

            // Assert
            assertAll("Sorted products should be returned correctly",
                () -> assertEquals(2, result.getContent().size(), "Should return 2 products"),
                () -> assertEquals("Apple", result.getContent().get(0).name(), "First product should be Apple"),
                () -> assertEquals("Banana", result.getContent().get(1).name(), "Second product should be Banana")
            );

            verify(productGateway, times(1)).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("Error Scenarios")
    class ErrorScenarios {

        @Test
        @DisplayName("Should handle gateway exceptions gracefully")
        void shouldHandleGatewayExceptionsGracefully() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);

            when(productGateway.findAll(pageable))
                .thenThrow(new RuntimeException("Database connection failed"));

            // Act & Assert
            assertThrows(
                RuntimeException.class,
                () -> listProductsUseCase.execute(pageable),
                "Should propagate runtime exceptions from gateway"
            );

            verify(productGateway, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Should handle null pageable")
        void shouldHandleNullPageable() {
            // Act & Assert
            assertThrows(
                IllegalArgumentException.class,
                () -> listProductsUseCase.execute(null),
                "Should throw exception for null pageable"
            );

            verify(productGateway, never()).findAll(any());
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should pass exact pageable to gateway")
        void shouldPassExactPageableToGateway() {
            // Arrange
            Pageable pageable = PageRequest.of(2, 15, 
                org.springframework.data.domain.Sort.by("price").descending());
            Page<Product> expectedPage = new PageImpl<>(List.of(), pageable, 0);

            when(productGateway.findAll(pageable)).thenReturn(expectedPage);

            // Act
            listProductsUseCase.execute(pageable);

            // Assert
            verify(productGateway, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Should return products with correct data structure")
        void shouldReturnProductsCorrectDataStructure() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<Product> products = List.of(
                new Product(1L, "Test Product", "TP001", new BigDecimal("999.99"), 42)
            );
            Page<Product> expectedPage = new PageImpl<>(products, pageable, 1);

            when(productGateway.findAll(pageable)).thenReturn(expectedPage);

            // Act
            Page<Product> result = listProductsUseCase.execute(pageable);

            // Assert
            Product product = result.getContent().get(0);
            assertAll("Product data structure should be correct",
                () -> assertEquals(1L, product.id(), "Product ID should be 1"),
                () -> assertEquals("Test Product", product.name(), "Product name should match"),
                () -> assertEquals("TP001", product.sku(), "Product SKU should match"),
                () -> assertEquals(new BigDecimal("999.99"), product.price(), "Product price should match"),
                () -> assertEquals(42, product.stock(), "Product stock should match"),
                () -> assertTrue(product.hasStock(40), "Product should have stock for 40 items"),
                () -> assertFalse(product.hasStock(50), "Product should not have stock for 50 items")
            );
        }
    }
}
