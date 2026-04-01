package com.ecommerce_backend.backend.infrastructure.dataprovider;

import com.ecommerce_backend.backend.core.domain.Order;
import com.ecommerce_backend.backend.core.domain.OrderStatus;
import com.ecommerce_backend.backend.core.gateway.OrderGateway;
import com.ecommerce_backend.backend.entrypoints.mapper.OrderMapper;
import com.ecommerce_backend.backend.infrastructure.dataprovider.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class OrderDataProvider implements OrderGateway {

    private final OrderRepository repository;
    private final OrderMapper mapper;

    public OrderDataProvider(OrderRepository repository, OrderMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    
    @Override
    public Page<Order> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDomain);
    }
    
    @Override
    @Transactional
    public Order save(Order order) {
        var entity = mapper.toEntity(order);

        if (entity.getItems() != null) {
            entity.getItems().forEach(item -> item.setOrder(entity));
        }

        var savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Order> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }
    
    @Override
    @Transactional
    public Order updateStatus(Long id, OrderStatus status) {
        var orderEntity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
        
        orderEntity.setStatus(status);
        var savedEntity = repository.save(orderEntity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public List<Order> findByProductId(Long productId) {
        return repository.findByItemsProductId(productId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}