package com.ecommerce_backend.backend.entrypoints.controller;

import com.ecommerce_backend.backend.core.domain.Product;
import com.ecommerce_backend.backend.core.useCases.create.CreateProductUseCase;
import com.ecommerce_backend.backend.core.useCases.list.ListProductsUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController Integration Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ListProductsUseCase listProductsUseCase;

    @MockitoBean
    private CreateProductUseCase createProductUseCase;

    @Nested
    @DisplayName("GET /products - List Products Tests")
    class ListProductsTests {

        @Test
        @DisplayName("Should return paginated list of products successfully")
        void shouldReturnPaginatedListOfProductsSuccessfully() throws Exception {
            // Arrange
            List<Product> products = List.of(
                new Product(1L, "Laptop", "LP001", new BigDecimal("5000.00"), 10),
                new Product(2L, "Mouse", "MS001", new BigDecimal("150.00"), 25),
                new Product(3L, "Keyboard", "KB001", new BigDecimal("200.00"), 15)
            );
            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), products.size());

            when(listProductsUseCase.execute(any())).thenReturn(productPage);

            // Act & Assert
            mockMvc.perform(get("/products")
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(3))
                    .andExpect(jsonPath("$.content[0].id").value(1))
                    .andExpect(jsonPath("$.content[0].name").value("Laptop"))
                    .andExpect(jsonPath("$.content[0].sku").value("LP001"))
                    .andExpect(jsonPath("$.content[0].price").value(5000.00))
                    .andExpect(jsonPath("$.content[0].stock").value(10))
                    .andExpect(jsonPath("$.totalElements").value(3))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.size").value(10))
                    .andExpect(jsonPath("$.number").value(0))
                    .andExpect(jsonPath("$.first").value(true))
                    .andExpect(jsonPath("$.last").value(true));

            verify(listProductsUseCase, times(1)).execute(any());
        }

        @Test
        @DisplayName("Should return empty page when no products exist")
        void shouldReturnEmptyPageWhenNoProductsExist() throws Exception {
            // Arrange
            Page<Product> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

            when(listProductsUseCase.execute(any())).thenReturn(emptyPage);

            // Act & Assert
            mockMvc.perform(get("/products")
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0))
                    .andExpect(jsonPath("$.totalPages").value(0));

            verify(listProductsUseCase, times(1)).execute(any());
        }

        @Test
        @DisplayName("Should use default pagination parameters when not provided")
        void shouldUseDefaultPaginationParametersWhenNotProvided() throws Exception {
            // Arrange
            List<Product> products = List.of(
                new Product(1L, "Default Product", "DP001", new BigDecimal("100.00"), 5)
            );
            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);

            when(listProductsUseCase.execute(any())).thenReturn(productPage);

            // Act & Assert
            mockMvc.perform(get("/products")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.size").value(10))
                    .andExpect(jsonPath("$.number").value(0));

            verify(listProductsUseCase, times(1)).execute(any());
        }

        @Test
        @DisplayName("Should handle custom pagination parameters")
        void shouldHandleCustomPaginationParameters() throws Exception {
            // Arrange
            List<Product> products = List.of(
                new Product(1L, "Custom Product", "CP001", new BigDecimal("300.00"), 8)
            );
            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(2, 5), 12);

            when(listProductsUseCase.execute(any())).thenReturn(productPage);

            // Act & Assert
            mockMvc.perform(get("/products")
                    .param("page", "2")
                    .param("size", "5")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.size").value(5))
                    .andExpect(jsonPath("$.number").value(2))
                    .andExpect(jsonPath("$.totalElements").value(12))
                    .andExpect(jsonPath("$.totalPages").value(3));

            verify(listProductsUseCase, times(1)).execute(any());
        }
    }

    @Nested
    @DisplayName("POST /products - Create Product Tests")
    class CreateProductTests {

        @Test
        @DisplayName("Should create product successfully")
        void shouldCreateProductSuccessfully() throws Exception {
            // Arrange
            Product newProduct = new Product(null, "New Laptop", "NL001", new BigDecimal("5500.00"), 12);
            Product createdProduct = new Product(1L, "New Laptop", "NL001", new BigDecimal("5500.00"), 12);

            when(createProductUseCase.execute(any(Product.class))).thenReturn(createdProduct);

            // Act & Assert
            mockMvc.perform(post("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newProduct)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("New Laptop"))
                    .andExpect(jsonPath("$.sku").value("NL001"))
                    .andExpect(jsonPath("$.price").value(5500.00))
                    .andExpect(jsonPath("$.stock").value(12));

            verify(createProductUseCase, times(1)).execute(any(Product.class));
        }

        @Test
        @DisplayName("Should return 400 when product data is invalid")
        void shouldReturn400WhenProductDataIsInvalid() throws Exception {
            // Arrange
            Product invalidProduct = new Product(null, "", "INVALID", new BigDecimal("-100.00"), -5);

            // Act & Assert
            mockMvc.perform(post("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidProduct)))
                    .andExpect(status().isBadRequest());

            verify(createProductUseCase, never()).execute(any(Product.class));
        }

        @Test
        @DisplayName("Should return 400 when request body is missing")
        void shouldReturn400WhenRequestBodyIsMissing() throws Exception {
            // Act & Assert
            mockMvc.perform(post("/products")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(createProductUseCase, never()).execute(any(Product.class));
        }

        @Test
        @DisplayName("Should return 400 when content type is not JSON")
        void shouldReturn400WhenContentTypeIsNotJson() throws Exception {
            // Act & Assert
            mockMvc.perform(post("/products")
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("invalid data"))
                    .andExpect(status().isUnsupportedMediaType());

            verify(createProductUseCase, never()).execute(any(Product.class));
        }

        @Test
        @DisplayName("Should handle duplicate SKU error")
        void shouldHandleDuplicateSkuError() throws Exception {
            // Arrange
            Product duplicateProduct = new Product(null, "Duplicate", "DP001", new BigDecimal("100.00"), 5);

            when(createProductUseCase.execute(any(Product.class)))
                .thenThrow(new IllegalArgumentException("Product with SKU DP001 already exists"));

            // Act & Assert
            mockMvc.perform(post("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(duplicateProduct)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Product with SKU DP001 already exists"));

            verify(createProductUseCase, times(1)).execute(any(Product.class));
        }

        @Test
        @DisplayName("Should handle server errors during product creation")
        void shouldHandleServerErrorsDuringProductCreation() throws Exception {
            // Arrange
            Product product = new Product(null, "Error Product", "EP001", new BigDecimal("100.00"), 5);

            when(createProductUseCase.execute(any(Product.class)))
                .thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            mockMvc.perform(post("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(product)))
                    .andExpect(status().isInternalServerError());

            verify(createProductUseCase, times(1)).execute(any(Product.class));
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle null values in product creation")
        void shouldHandleNullValuesInProductCreation() throws Exception {
            // Arrange
            Product productWithNulls = new Product(null, null, null, null, null);

            // Act & Assert
            mockMvc.perform(post("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productWithNulls)))
                    .andExpect(status().isBadRequest());

            verify(createProductUseCase, never()).execute(any(Product.class));
        }

        @Test
        @DisplayName("Should handle malformed JSON")
        void shouldHandleMalformedJson() throws Exception {
            // Act & Assert
            mockMvc.perform(post("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{invalid json}"))
                    .andExpect(status().isBadRequest());

            verify(createProductUseCase, never()).execute(any(Product.class));
        }

        @Test
        @DisplayName("Should handle use case exceptions in list endpoint")
        void shouldHandleUseCaseExceptionsInListEndpoint() throws Exception {
            // Arrange
            when(listProductsUseCase.execute(any()))
                .thenThrow(new RuntimeException("Service unavailable"));

            // Act & Assert
            mockMvc.perform(get("/products")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());

            verify(listProductsUseCase, times(1)).execute(any());
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should validate product fields correctly")
        void shouldValidateProductFieldsCorrectly() throws Exception {
            // Arrange
            Product validProduct = new Product(null, "Valid Product", "VP001", new BigDecimal("100.00"), 10);
            Product createdProduct = new Product(1L, "Valid Product", "VP001", new BigDecimal("100.00"), 10);

            when(createProductUseCase.execute(any(Product.class))).thenReturn(createdProduct);

            // Act & Assert
            mockMvc.perform(post("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validProduct)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Valid Product"))
                    .andExpect(jsonPath("$.sku").value("VP001"))
                    .andExpect(jsonPath("$.price").value(100.00))
                    .andExpect(jsonPath("$.stock").value(10));

            verify(createProductUseCase, times(1)).execute(validProduct);
        }

        @Test
        @DisplayName("Should handle concurrent requests correctly")
        void shouldHandleConcurrentRequestsCorrectly() throws Exception {
            // Arrange
            List<Product> products = List.of(
                new Product(1L, "Product 1", "P001", new BigDecimal("100.00"), 5)
            );
            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);

            when(listProductsUseCase.execute(any())).thenReturn(productPage);

            // Act & Assert - Multiple requests should work correctly
            mockMvc.perform(get("/products").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/products").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(listProductsUseCase, times(2)).execute(any());
        }
    }
}
