package com.ecommerce_backend.backend.infrastructure.dataprovider.repository;

import com.ecommerce_backend.backend.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional; // Não esqueça o import

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    // Adicione esta linha para o Spring gerar a busca por SKU
    Optional<ProductEntity> findBySku(String sku);
}