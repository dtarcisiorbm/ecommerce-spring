package com.ecommerce_backend.backend.infrastructure.cache;

import com.ecommerce_backend.backend.core.domain.Product;
import com.ecommerce_backend.backend.core.gateway.ProductGateway;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductCacheService {

    private final CacheService cacheService;
    private final ProductGateway productGateway;

    public ProductCacheService(CacheService cacheService, ProductGateway productGateway) {
        this.cacheService = cacheService;
        this.productGateway = productGateway;
    }

    public Optional<Product> getProductById(UUID productId) {
        // Tentar do cache primeiro
        Optional<Object> cached = cacheService.getCachedProduct(productId.toString());
        if (cached.isPresent()) {
            return Optional.of((Product) cached.get());
        }

        // Se não estiver no cache, buscar do banco
        Optional<Product> product = productGateway.findById(productId);
        
        // Se encontrar, adicionar ao cache
        product.ifPresent(p -> cacheService.cacheProduct(productId.toString(), p));
        
        return product;
    }

    public void evictProduct(UUID productId) {
        cacheService.evictProduct(productId.toString());
    }

    public void cachePopularProducts(List<Product> products) {
        cacheService.cacheCategories("popular", products);
    }

    public Optional<List<Product>> getPopularProducts() {
        Optional<Object> cached = cacheService.getCachedCategories("popular");
        return cached.map(obj -> (List<Product>) obj);
    }

    public void cacheProductsByCategory(UUID categoryId, List<Product> products) {
        cacheService.cacheCategories("category:" + categoryId, products);
    }

    public Optional<List<Product>> getProductsByCategory(UUID categoryId) {
        Optional<Object> cached = cacheService.getCachedCategories("category:" + categoryId);
        return cached.map(obj -> (List<Product>) obj);
    }

    public void evictProductsByCategory(UUID categoryId) {
        cacheService.evictCategories("category:" + categoryId);
    }

    public void cacheSearchResults(String searchKey, List<Product> results) {
        cacheService.cacheSearchResult(searchKey, results);
    }

    public Optional<List<Product>> getSearchResults(String searchKey) {
        Optional<Object> cached = cacheService.getCachedSearchResult(searchKey);
        return cached.map(obj -> (List<Product>) obj);
    }

    // Método para pré-carregar produtos populares
    public void preloadPopularProducts() {
        // Em um caso real, buscaríamos os produtos mais vendidos ou mais visualizados
        // Por ora, vamos buscar os primeiros 10 produtos
        List<Product> popularProducts = productGateway.findAll(
                org.springframework.data.domain.PageRequest.of(0, 10)
        ).getContent();
        
        cachePopularProducts(popularProducts);
    }
}
