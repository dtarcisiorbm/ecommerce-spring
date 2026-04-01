package com.ecommerce_backend.backend.infrastructure.config;

import com.ecommerce_backend.backend.core.gateway.CategoryGateway;
import com.ecommerce_backend.backend.core.useCases.create.CreateCategoryUseCase;
import com.ecommerce_backend.backend.core.useCases.delete.DeleteCategoryUseCase;
import com.ecommerce_backend.backend.core.useCases.list.ListCategoriesUseCase;
import com.ecommerce_backend.backend.core.useCases.update.UpdateCategoryUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryConfig {

    @Bean
    public CreateCategoryUseCase createCategoryUseCase(CategoryGateway categoryGateway) {
        return new CreateCategoryUseCase(categoryGateway);
    }

    @Bean
    public ListCategoriesUseCase listCategoriesUseCase(CategoryGateway categoryGateway) {
        return new ListCategoriesUseCase(categoryGateway);
    }

    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase(CategoryGateway categoryGateway) {
        return new UpdateCategoryUseCase(categoryGateway);
    }

    @Bean
    public DeleteCategoryUseCase deleteCategoryUseCase(CategoryGateway categoryGateway) {
        return new DeleteCategoryUseCase(categoryGateway);
    }
}
