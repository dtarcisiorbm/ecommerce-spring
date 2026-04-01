package com.ecommerce_backend.backend.entrypoints.controller;

import com.ecommerce_backend.backend.core.domain.Customer;
import com.ecommerce_backend.backend.core.domain.Order;
import com.ecommerce_backend.backend.core.domain.OrderItem;

import com.ecommerce_backend.backend.core.useCases.create.CreateOrderUseCase;
import com.ecommerce_backend.backend.core.useCases.list.ListOrdersUseCase;
import com.ecommerce_backend.backend.core.useCases.update.UpdateOrderStatusUseCase;
import com.ecommerce_backend.backend.entrypoints.dto.OrderRequest;
import com.ecommerce_backend.backend.entrypoints.dto.OrderStatusUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final ListOrdersUseCase listOrdersUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;

    public OrderController(
            CreateOrderUseCase createOrderUseCase,
            ListOrdersUseCase listOrdersUseCase,
            UpdateOrderStatusUseCase updateOrderStatusUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.listOrdersUseCase = listOrdersUseCase;
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
    }
    
    @GetMapping
    public ResponseEntity<Page<Order>> list(
            @PageableDefault(size = 10, page = 0, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(listOrdersUseCase.execute(pageable));
    }
    
    @PostMapping
    public ResponseEntity<Order> create(@RequestBody @Valid OrderRequest request) {
        try {
            // 1. Mapeamento manual do DTO para o Domínio (OrderItem)
            var items = request.items().stream()
                    .map(item -> new OrderItem(
                            null,
                            item.productId(),
                            item.quantity(),
                            item.unitPrice()))
                    .collect(Collectors.toList());

            // 2. Mapeamento para o Domínio Order
            var orderDomain = new Order(
                    null,
                    new Customer(request.customerId(), null, null, null, null),
                    items,
                    null,
                    null
            );

            // 3. Execução do Caso de Uso que já valida estoque e status
            Order createdOrder = createOrderUseCase.execute(orderDomain);

            // 4. Retorno com status 201 Created
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (IllegalArgumentException ex) {
            throw ex; // Deixa o GlobalExceptionHandler tratar
        } catch (IllegalStateException ex) {
            throw ex; // Deixa o GlobalExceptionHandler tratar
        } catch (RuntimeException ex) {
            throw new RuntimeException("Failed to create order", ex);
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid OrderStatusUpdateRequest request) {
        try {
            Order updatedOrder = updateOrderStatusUseCase.execute(id, request.status());
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException ex) {
            throw ex; // Deixa o GlobalExceptionHandler tratar
        } catch (RuntimeException ex) {
            throw new RuntimeException("Failed to update order status", ex);
        }
    }
}
