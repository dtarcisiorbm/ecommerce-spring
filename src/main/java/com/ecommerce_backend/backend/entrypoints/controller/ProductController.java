package com.ecommerce_backend.backend.entrypoints.controller;

import com.ecommerce_backend.backend.core.domain.Product;

import com.ecommerce_backend.backend.core.useCases.create.CreateProductUseCase;
import com.ecommerce_backend.backend.core.useCases.list.ListProductsUseCase;
// Importe um Use Case para busca por ID se existir
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ListProductsUseCase listProductsUseCase;
    private final CreateProductUseCase createProductUseCase;
    // private final FindProductByIdUseCase findProductByIdUseCase; // Sugestão

    // APENAS UM CONSTRUTOR para todas as dependências
    public ProductController(
            ListProductsUseCase listProductsUseCase,
            CreateProductUseCase createProductUseCase) {
        this.listProductsUseCase = listProductsUseCase;
        this.createProductUseCase = createProductUseCase;
    }

    @GetMapping
    public ResponseEntity<Page<Product>> list(
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(listProductsUseCase.execute(pageable));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Valid Product productRequest) {
        try {
            // O ideal aqui seria usar um ProductRequestDTO e mapear para o domínio
            Product created = createProductUseCase.execute(productRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            throw ex; // Deixa o GlobalExceptionHandler tratar
        } catch (RuntimeException ex) {
            throw new RuntimeException("Failed to create product", ex);
        }
    }

    /* @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        // Em vez de chamar o Repository, chame um Use Case
        return findProductByIdUseCase.execute(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    */
}