package com.ecommerce_backend.backend.entrypoints.controller;

import com.ecommerce_backend.backend.core.domain.Customer;
import com.ecommerce_backend.backend.core.gateway.CustomerGateway;
import com.ecommerce_backend.backend.entrypoints.dto.CustomerRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerGateway customerGateway;

    public CustomerController(CustomerGateway customerGateway) {
        this.customerGateway = customerGateway;
    }

    /**
     * Lista todos os clientes (apenas administradores)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Customer>> listAll() {
        // Implementação básica - você pode adicionar paginação depois
        return ResponseEntity.ok(List.of());
    }

    /**
     * Busca cliente por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @customerSecurity.canAccess(#id, authentication))")
    public ResponseEntity<Customer> findById(@PathVariable Long id) {
        return customerGateway.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Atualiza dados do cliente
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @customerSecurity.canAccess(#id, authentication))")
    public ResponseEntity<Customer> update(@PathVariable Long id, @RequestBody @Valid CustomerRequest request) {
        Customer customer = new Customer(
                id,
                request.fullName(),
                request.email(),
                request.taxId(),
                request.password()
        );

        Customer updated = customerGateway.save(customer);
        return ResponseEntity.ok(updated);
    }

    /**
     * Remove cliente (apenas administradores)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerGateway.findById(id).ifPresent(customer -> {
            // Aqui você implementaria a lógica de delete
            // Por enquanto, apenas simulamos
        });
        return ResponseEntity.noContent().build();
    }
}