package com.ecommerce_backend.backend.infrastructure.dataprovider.repository;

import com.ecommerce_backend.backend.infrastructure.persistence.entity.ShoppingCartItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItemEntity, UUID> {
    List<ShoppingCartItemEntity> findByShoppingCartId(UUID shoppingCartId);
    
    @Query("SELECT item FROM ShoppingCartItemEntity item WHERE item.shoppingCart.id = :shoppingCartId AND item.productId = :productId")
    Optional<ShoppingCartItemEntity> findByShoppingCartIdAndProductId(@Param("shoppingCartId") UUID shoppingCartId, 
                                                               @Param("productId") UUID productId);
    
    @Query("SELECT item FROM ShoppingCartItemEntity item WHERE item.shoppingCart.customerId = :customerId")
    Page<ShoppingCartItemEntity> findByCustomerId(@Param("customerId") UUID customerId, Pageable pageable);
}
