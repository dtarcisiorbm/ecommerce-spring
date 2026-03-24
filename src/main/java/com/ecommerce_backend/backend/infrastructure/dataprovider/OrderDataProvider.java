package com.ecommerce_backend.backend.infrastructure.dataprovider;

import com.ecommerce_backend.backend.core.domain.Order;
import com.ecommerce_backend.backend.core.gateway.OrderGateway;
import com.ecommerce_backend.backend.entrypoints.mapper.OrderMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderDataProvider implements OrderGateway {

    private final OrderRepository repository;
    private final OrderMapper mapper;

    public OrderDataProvider(OrderRepository repository, OrderMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional // Garante que a ordem e os itens sejam salvos na mesma transação
    public Order save(Order order) {
        var entity = mapper.toEntity(order);

        // Importante: Vincular cada item à entidade pai (Order) para o JPA mapear a FK
        if (entity.getItems() != null) {
            entity.getItems().forEach(item -> item.setOrder(entity));
        }

        var savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}