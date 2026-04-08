package com.ecommerce_backend.backend.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthenticatedUser {

    /**
     * Obtém o ID do cliente autenticado a partir do contexto de segurança.
     * Para clientes CUSTOMER, extrai o ID dos detalhes da autenticação.
     * Para usuários ADMIN, retorna null (pois não são clientes).
     * 
     * @return UUID do cliente ou null se não for um cliente
     * @throws IllegalStateException se não houver usuário autenticado
     */
    public UUID getCurrentCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        // Verifica se o usuário tem role CUSTOMER
        boolean isCustomer = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CUSTOMER"));
        
        if (!isCustomer) {
            return null; // Usuário ADMIN não tem customerId
        }

        // Extrai o customerId dos detalhes da autenticação
        Object details = authentication.getDetails();
        if (details instanceof UUID) {
            return (UUID) details;
        }

        throw new IllegalStateException("Customer ID not found in authentication details");
    }

    /**
     * Obtém o email do usuário autenticado.
     * 
     * @return Email do usuário autenticado
     * @throws IllegalStateException se não houver usuário autenticado
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        return authentication.getName();
    }

    /**
     * Verifica se o usuário autenticado é um CLIENTE.
     * 
     * @return true se for CUSTOMER, false caso contrário
     */
    public boolean isCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CUSTOMER"));
    }

    /**
     * Verifica se o usuário autenticado é um ADMINISTRADOR.
     * 
     * @return true se for ADMIN, false caso contrário
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
