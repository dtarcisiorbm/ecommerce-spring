package com.ecommerce_backend.backend.infrastructure.dataprovider;

import com.ecommerce_backend.backend.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
