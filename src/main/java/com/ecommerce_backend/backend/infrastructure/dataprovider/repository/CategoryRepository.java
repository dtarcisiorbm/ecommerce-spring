package com.ecommerce_backend.backend.infrastructure.dataprovider.repository;

import com.ecommerce_backend.backend.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    Optional<CategoryEntity> findByName(String name);
    List<CategoryEntity> findByParentId(UUID parentId);
    List<CategoryEntity> findByParentIdIsNull();
    boolean existsByName(String name);
    
    @Query("SELECT c FROM CategoryEntity c WHERE c.name = :name AND c.id != :id")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") UUID id);
    
    @Transactional
    @Modifying
    @Query("UPDATE CategoryEntity c SET c.active = false WHERE c.id = :id")
    void softDeleteById(@Param("id") UUID id);
    
    @Query("SELECT c FROM CategoryEntity c WHERE c.active = true ORDER BY c.name")
    List<CategoryEntity> findAllActive();
}
