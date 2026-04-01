package com.ecommerce_backend.backend.entrypoints.controller;

import com.ecommerce_backend.backend.core.domain.Customer;
import com.ecommerce_backend.backend.core.gateway.CustomerGateway;
import com.ecommerce_backend.backend.core.useCases.delete.DeleteCustomerUseCase;
import com.ecommerce_backend.backend.core.useCases.find.FindCustomerByIdUseCase;
import com.ecommerce_backend.backend.core.useCases.list.ListCustomersUseCase;
import com.ecommerce_backend.backend.core.useCases.update.UpdateCustomerUseCase;
import com.ecommerce_backend.backend.entrypoints.dto.CustomerRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final ListCustomersUseCase listCustomersUseCase;
    private final FindCustomerByIdUseCase findCustomerByIdUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;
    private final CustomerGateway customerGateway;

    public CustomerController(
            ListCustomersUseCase listCustomersUseCase,
            FindCustomerByIdUseCase findCustomerByIdUseCase,
            UpdateCustomerUseCase updateCustomerUseCase,
            DeleteCustomerUseCase deleteCustomerUseCase,
            CustomerGateway customerGateway) {
        this.listCustomersUseCase = listCustomersUseCase;
        this.findCustomerByIdUseCase = findCustomerByIdUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
        this.deleteCustomerUseCase = deleteCustomerUseCase;
        this.customerGateway = customerGateway;
    }

    /**
     * Lista todos os clientes (apenas administradores)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Customer>> listAll(
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(listCustomersUseCase.execute(pageable));
    }

    /**
     * Lista apenas clientes ativos
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<Customer>> listActive(
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(listCustomersUseCase.execute(pageable));
    }

    /**
     * Busca cliente por ID (apenas se estiver ativo)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @customerSecurity.canAccess(#id, authentication))")
    public ResponseEntity<Customer> findById(@PathVariable UUID id) {
        return customerGateway.findByIdAndActive(id, true)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Atualiza dados do cliente
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @customerSecurity.canAccess(#id, authentication))")
    public ResponseEntity<Customer> update(@PathVariable UUID id, @RequestBody @Valid CustomerRequest request) {
        Customer customer = new Customer(
                id,
                request.fullName(),
                request.email(),
                request.taxId(),
                request.password()
        );

        Customer updated = updateCustomerUseCase.execute(customer);
        return ResponseEntity.ok(updated);
    }

    /**
     * Remove cliente (soft delete - mantém histórico)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteCustomerUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}