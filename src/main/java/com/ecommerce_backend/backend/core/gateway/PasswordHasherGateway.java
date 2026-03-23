package com.ecommerce_backend.backend.core.gateway;

public interface PasswordHasherGateway {

    String hash(String plainText);

    boolean matches(String plainText, String hashedText);
}