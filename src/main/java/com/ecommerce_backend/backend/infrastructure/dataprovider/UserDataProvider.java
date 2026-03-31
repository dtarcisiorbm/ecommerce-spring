package com.ecommerce_backend.backend.infrastructure.dataprovider;

import com.ecommerce_backend.backend.core.domain.User;
import com.ecommerce_backend.backend.core.gateway.UserGateway;
import com.ecommerce_backend.backend.entrypoints.mapper.UserMapper;
import com.ecommerce_backend.backend.infrastructure.dataprovider.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class UserDataProvider implements UserGateway {

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserDataProvider(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        var entity = mapper.toEntity(user);
        var savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    @Override
    public Page<User> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDomain);
    }
    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toDomain);
    }
}