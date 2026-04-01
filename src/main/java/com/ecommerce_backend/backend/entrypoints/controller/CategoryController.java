package com.ecommerce_backend.backend.entrypoints.controller;

import com.ecommerce_backend.backend.core.domain.Category;
import com.ecommerce_backend.backend.core.useCases.create.CreateCategoryUseCase;
import com.ecommerce_backend.backend.core.useCases.delete.DeleteCategoryUseCase;
import com.ecommerce_backend.backend.core.useCases.list.ListCategoriesUseCase;
import com.ecommerce_backend.backend.core.useCases.update.UpdateCategoryUseCase;
import com.ecommerce_backend.backend.entrypoints.dto.CategoryRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    public CategoryController(
            CreateCategoryUseCase createCategoryUseCase,
            ListCategoriesUseCase listCategoriesUseCase,
            UpdateCategoryUseCase updateCategoryUseCase,
            DeleteCategoryUseCase deleteCategoryUseCase) {
        this.createCategoryUseCase = createCategoryUseCase;
        this.listCategoriesUseCase = listCategoriesUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
    }

    /**
     * Lista todas as categorias (paginado)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<Category>> listAll(
            @PageableDefault(size = 20, page = 0) Pageable pageable) {
        return ResponseEntity.ok(listCategoriesUseCase.execute(pageable));
    }

    /**
     * Lista apenas categorias ativas
     */
    @GetMapping("/active")
    public ResponseEntity<List<Category>> listActive() {
        return ResponseEntity.ok(listCategoriesUseCase.executeActive());
    }

    /**
     * Lista categorias raiz (sem pai)
     */
    @GetMapping("/root")
    public ResponseEntity<List<Category>> listRootCategories() {
        return ResponseEntity.ok(listCategoriesUseCase.executeRootCategories());
    }

    /**
     * Lista subcategorias de uma categoria pai
     */
    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<List<Category>> listSubcategories(@PathVariable UUID parentId) {
        return ResponseEntity.ok(listCategoriesUseCase.executeByParent(parentId));
    }

    /**
     * Busca categoria por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Category> findById(@PathVariable UUID id) {
        return listCategoriesUseCase.executeActive()
                .stream()
                .filter(cat -> cat.id().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cria nova categoria
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Category> create(@RequestBody @Valid CategoryRequest request) {
        Category category = new Category(
                null,
                request.name(),
                request.description(),
                request.parentId(),
                true,
                null,
                null
        );

        Category created = createCategoryUseCase.execute(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Atualiza categoria existente
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Category> update(
            @PathVariable UUID id,
            @RequestBody @Valid CategoryRequest request) {
        Category category = new Category(
                id,
                request.name(),
                request.description(),
                request.parentId(),
                true,
                null,
                null
        );

        Category updated = updateCategoryUseCase.execute(category);
        return ResponseEntity.ok(updated);
    }

    /**
     * Remove categoria (soft delete)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteCategoryUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
