package com.ecommerce_backend.backend.infrastructure.dataprovider;

import com.ecommerce_backend.backend.core.domain.Order;
import com.ecommerce_backend.backend.core.gateway.OrderGateway;
import com.ecommerce_backend.backend.entrypoints.mapper.OrderMapper;
import com.ecommerce_backend.backend.infrastructure.dataprovider.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<Order> findAll(Pageable pageable) {
        // Procura as entidades e utiliza o mapper para converter cada uma para o domínio
        return repository.findAll(pageable)
                .map(mapper::toDomain);
    }
    // Arquivo: src/main/java/com/ecommerce_backend/backend/infrastructure/dataprovider/OrderDataProvider.java
    @Override
    @Transactional // Garante que se houver erro nos itens, nada é salvo
    public Order save(Order order) {
        var entity = mapper.toEntity(order);

        // Vincula cada item à entidade pai para que o JPA saiba onde inserir a FK
        if (entity.getItems() != null) {
            entity.getItems().forEach(item -> item.setOrder(entity));
        }

        var savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}