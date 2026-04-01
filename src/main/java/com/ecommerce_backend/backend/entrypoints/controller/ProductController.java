package com.ecommerce_backend.backend.entrypoints.controller;

import com.ecommerce_backend.backend.core.domain.Product;

import com.ecommerce_backend.backend.core.useCases.create.CreateProductUseCase;
import com.ecommerce_backend.backend.core.useCases.delete.DeleteProductUseCase;
import com.ecommerce_backend.backend.core.useCases.find.FindProductByIdUseCase;
import com.ecommerce_backend.backend.core.useCases.list.ListProductsUseCase;
import com.ecommerce_backend.backend.core.useCases.update.UpdateProductUseCase;
import com.ecommerce_backend.backend.core.useCases.search.SearchProductsUseCase;
import com.ecommerce_backend.backend.entrypoints.dto.ProductRequest;
import com.ecommerce_backend.backend.entrypoints.dto.ProductFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ListProductsUseCase listProductsUseCase;
    private final CreateProductUseCase createProductUseCase;
    private final FindProductByIdUseCase findProductByIdUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final SearchProductsUseCase searchProductsUseCase;

    public ProductController(
            ListProductsUseCase listProductsUseCase,
            CreateProductUseCase createProductUseCase,
            FindProductByIdUseCase findProductByIdUseCase,
            UpdateProductUseCase updateProductUseCase,
            DeleteProductUseCase deleteProductUseCase,
            SearchProductsUseCase searchProductsUseCase) {
        this.listProductsUseCase = listProductsUseCase;
        this.createProductUseCase = createProductUseCase;
        this.findProductByIdUseCase = findProductByIdUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.searchProductsUseCase = searchProductsUseCase;
    }

    @GetMapping
    public ResponseEntity<Page<Product>> list(
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(listProductsUseCase.execute(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID id) {
        return findProductByIdUseCase.execute(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductRequest request) {
        try {
            Product product = new Product(
                    null,
                    request.name(),
                    request.sku(),
                    request.price(),
                    request.stock(),
                    request.categoryId()
            );
            
            Product created = createProductUseCase.execute(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            throw ex; // Deixa o GlobalExceptionHandler tratar
        } catch (RuntimeException ex) {
            throw new RuntimeException("Failed to create product", ex);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable UUID id, 
            @RequestBody @Valid ProductRequest request) {
        try {
            Product product = new Product(
                    id,
                    request.name(),
                    request.sku(),
                    request.price(),
                    request.stock(),
                    request.categoryId()
            );
            
            Product updated = updateProductUseCase.execute(id, product);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            throw ex; // Deixa o GlobalExceptionHandler tratar
        } catch (RuntimeException ex) {
            throw new RuntimeException("Failed to update product", ex);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        try {
            deleteProductUseCase.execute(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            throw ex; // Deixa o GlobalExceptionHandler tratar
        } catch (RuntimeException ex) {
            throw new RuntimeException("Failed to delete product", ex);
        }
    }
    
    /**
     * Busca produtos por nome
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchByName(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        if (name != null && !name.trim().isEmpty()) {
            return ResponseEntity.ok(searchProductsUseCase.searchByName(name, pageable));
        }
        return ResponseEntity.ok(listProductsUseCase.execute(pageable));
    }
    
    /**
     * Busca produtos por categoria
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<Product>> searchByCategory(
            @PathVariable UUID categoryId,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(searchProductsUseCase.searchByCategory(categoryId, pageable));
    }
    
    /**
     * Busca produtos com múltiplos filtros
     */
    @PostMapping("/filter")
    public ResponseEntity<Page<Product>> filter(
            @RequestBody(required = false) ProductFilterRequest filters,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(searchProductsUseCase.execute(filters, pageable));
    }
}
