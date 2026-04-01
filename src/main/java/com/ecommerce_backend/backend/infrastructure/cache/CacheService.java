package com.ecommerce_backend.backend.infrastructure.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Service
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Cache de produtos
    public void cacheProduct(String productId, Object product) {
        String key = "product:" + productId;
        redisTemplate.opsForValue().set(key, product, Duration.ofHours(1));
    }

    public Optional<Object> getCachedProduct(String productId) {
        String key = "product:" + productId;
        Object value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(value);
    }

    public void evictProduct(String productId) {
        String key = "product:" + productId;
        redisTemplate.delete(key);
    }

    // Cache de categorias
    public void cacheCategory(String categoryId, Object category) {
        String key = "category:" + categoryId;
        redisTemplate.opsForValue().set(key, category, Duration.ofHours(2));
    }

    public Optional<Object> getCachedCategory(String categoryId) {
        String key = "category:" + categoryId;
        Object value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(value);
    }

    public void evictCategory(String categoryId) {
        String key = "category:" + categoryId;
        redisTemplate.delete(key);
    }

    // Cache de listas de categorias
    public void cacheCategories(String key, Object categories) {
        redisTemplate.opsForValue().set("categories:" + key, categories, Duration.ofMinutes(30));
    }

    public Optional<Object> getCachedCategories(String key) {
        Object value = redisTemplate.opsForValue().get("categories:" + key);
        return Optional.ofNullable(value);
    }

    public void evictCategories(String key) {
        redisTemplate.delete("categories:" + key);
    }

    // Cache de resultados de busca
    public void cacheSearchResult(String searchKey, Object result) {
        redisTemplate.opsForValue().set("search:" + searchKey, result, Duration.ofMinutes(15));
    }

    public Optional<Object> getCachedSearchResult(String searchKey) {
        Object value = redisTemplate.opsForValue().get("search:" + searchKey);
        return Optional.ofNullable(value);
    }

    // Cache de sessões de usuário
    public void cacheUserSession(String userId, Object sessionData) {
        redisTemplate.opsForValue().set("session:" + userId, sessionData, Duration.ofMinutes(30));
    }

    public Optional<Object> getCachedUserSession(String userId) {
        Object value = redisTemplate.opsForValue().get("session:" + userId);
        return Optional.ofNullable(value);
    }

    public void evictUserSession(String userId) {
        redisTemplate.delete("session:" + userId);
    }

    // Cache de carrinho de compras
    public void cacheShoppingCart(String userId, Object cartData) {
        redisTemplate.opsForValue().set("cart:" + userId, cartData, Duration.ofHours(2));
    }

    public Optional<Object> getCachedShoppingCart(String userId) {
        Object value = redisTemplate.opsForValue().get("cart:" + userId);
        return Optional.ofNullable(value);
    }

    public void evictShoppingCart(String userId) {
        redisTemplate.delete("cart:" + userId);
    }

    // Métodos utilitários
    public void clearCacheByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setWithExpiration(String key, Object value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    // Cache de rate limiting
    public void incrementRateLimit(String key, Duration window) {
        redisTemplate.opsForValue().setIfAbsent(key + ":count", 0, window);
        redisTemplate.opsForValue().increment(key + ":count");
    }

    public Integer getRateLimitCount(String key) {
        Object value = redisTemplate.opsForValue().get(key + ":count");
        return value != null ? Integer.valueOf(value.toString()) : 0;
    }

    public void resetRateLimit(String key) {
        redisTemplate.delete(key + ":count");
    }
}
