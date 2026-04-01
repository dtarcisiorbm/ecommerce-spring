package com.ecommerce_backend.backend.infrastructure.dataprovider;

import com.ecommerce_backend.backend.core.domain.ShoppingCart;
import com.ecommerce_backend.backend.core.domain.ShoppingCartItem;
import com.ecommerce_backend.backend.core.gateway.ShoppingCartGateway;
import com.ecommerce_backend.backend.entrypoints.mapper.ShoppingCartMapper;
import com.ecommerce_backend.backend.entrypoints.mapper.ShoppingCartItemMapper;
import com.ecommerce_backend.backend.infrastructure.dataprovider.repository.ShoppingCartRepository;
import com.ecommerce_backend.backend.infrastructure.dataprovider.repository.ShoppingCartItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ShoppingCartDataProvider implements ShoppingCartGateway {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartItemRepository shoppingCartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final ShoppingCartItemMapper shoppingCartItemMapper;

    public ShoppingCartDataProvider(
            ShoppingCartRepository shoppingCartRepository,
            ShoppingCartItemRepository shoppingCartItemRepository,
            ShoppingCartMapper shoppingCartMapper,
            ShoppingCartItemMapper shoppingCartItemMapper) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.shoppingCartItemRepository = shoppingCartItemRepository;
        this.shoppingCartMapper = shoppingCartMapper;
        this.shoppingCartItemMapper = shoppingCartItemMapper;
    }

    // Carrinho
    @Override
    public ShoppingCart save(ShoppingCart shoppingCart) {
        var entity = shoppingCartMapper.toEntity(shoppingCart);
        var savedEntity = shoppingCartRepository.save(entity);
        return shoppingCartMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ShoppingCart> findByCustomerId(UUID customerId) {
        return shoppingCartRepository.findByCustomerId(customerId)
                .map(shoppingCartMapper::toDomain);
    }

    @Override
    public Optional<ShoppingCart> findById(UUID id) {
        return shoppingCartRepository.findById(id)
                .map(shoppingCartMapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        shoppingCartItemRepository.findByShoppingCartId(id)
                .forEach(item -> shoppingCartItemRepository.deleteById(item.getId()));
        shoppingCartRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void clearCart(UUID customerId) {
        shoppingCartRepository.findByCustomerId(customerId)
                .ifPresent(cart -> deleteById(cart.getId()));
    }

    // Itens do Carrinho
    @Override
    @Transactional
    public ShoppingCartItem addItem(ShoppingCartItem item) {
        var entity = shoppingCartItemMapper.toEntity(item);
        var savedEntity = shoppingCartItemRepository.save(entity);
        return shoppingCartItemMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public ShoppingCartItem updateItem(ShoppingCartItem item) {
        var entity = shoppingCartItemMapper.toEntity(item);
        var savedEntity = shoppingCartItemRepository.save(entity);
        return shoppingCartItemMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public void removeItem(UUID itemId) {
        shoppingCartItemRepository.deleteById(itemId);
    }

    @Override
    public List<ShoppingCartItem> findByShoppingCartId(UUID shoppingCartId) {
        return shoppingCartItemRepository.findByShoppingCartId(shoppingCartId)
                .stream()
                .map(shoppingCartItemMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ShoppingCartItem> findByShoppingCartIdAndProductId(UUID shoppingCartId, UUID productId) {
        return shoppingCartItemRepository.findByShoppingCartIdAndProductId(shoppingCartId, productId)
                .map(shoppingCartItemMapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteItem(UUID itemId) {
        shoppingCartItemRepository.deleteById(itemId);
    }

    @Override
    public Page<ShoppingCartItem> findAllItems(Pageable pageable) {
        return shoppingCartItemRepository.findAll(pageable)
                .map(shoppingCartItemMapper::toDomain);
    }
}
