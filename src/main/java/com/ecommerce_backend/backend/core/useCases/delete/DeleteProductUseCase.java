package com.ecommerce_backend.backend.core.useCases.delete;

import com.ecommerce_backend.backend.core.domain.Order;
import com.ecommerce_backend.backend.core.domain.OrderStatus;
import com.ecommerce_backend.backend.core.domain.Product;
import com.ecommerce_backend.backend.core.gateway.OrderGateway;
import com.ecommerce_backend.backend.core.gateway.ProductGateway;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeleteProductUseCase {
    private final ProductGateway productGateway;
    private final OrderGateway orderGateway;

    public DeleteProductUseCase(ProductGateway productGateway, OrderGateway orderGateway) {
        this.productGateway = productGateway;
        this.orderGateway = orderGateway;
    }

    public void execute(UUID id) {
        Optional<Product> product = productGateway.findById(id);
        if (product.isEmpty()) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        
        // Verificar se o produto está em algum pedido antes de excluir
        List<Order> ordersWithProduct = orderGateway.findByProductId(id);
        if (!ordersWithProduct.isEmpty()) {
            // Verificar se todos os pedidos com este produto estão cancelados
            boolean hasActiveOrders = ordersWithProduct.stream()
                    .anyMatch(order -> order.status() != OrderStatus.CANCELED);
            
            if (hasActiveOrders) {
                throw new IllegalStateException(
                    "Cannot delete product: it is associated with active orders. " +
                    "Product is present in " + ordersWithProduct.size() + " order(s)."
                );
            }
        }
        
        productGateway.deleteById(id);
    }
}
