package com.ecommerce_backend.backend.core.domain;

public enum UserRole {
    ADMIN("ADMIN"),
    MANAGER("MANAGER"), 
    CUSTOMER("CUSTOMER"),
    USER("USER");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public String getAuthority() {
        return "ROLE_" + role;
    }
}
