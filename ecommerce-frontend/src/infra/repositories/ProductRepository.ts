import apiClient from '../api/client';
import { Product, ProductFilter, ProductFormData, PaginatedResponse } from '@/domain/entities/Product';
import { PaginatedRequest } from '@/domain/types/api';

export const ProductRepository = {
  async list(params: PaginatedRequest = { page: 0, size: 10 }): Promise<PaginatedResponse<Product>> {
    const { data } = await apiClient.get<PaginatedResponse<Product>>('/products', {
      params: { page: params.page, size: params.size, sort: params.sort },
    });
    return data;
  },

  async getById(id: string): Promise<Product | null> {
    try {
      const { data } = await apiClient.get<Product>(`/products/${id}`);
      return data;
    } catch {
      return null;
    }
  },

  async create(product: ProductFormData): Promise<Product> {
    const { data } = await apiClient.post<Product>('/products', product);
    return data;
  },

  async update(id: string, product: ProductFormData): Promise<Product> {
    const { data } = await apiClient.put<Product>(`/products/${id}`, product);
    return data;
  },

  async delete(id: string): Promise<void> {
    await apiClient.delete(`/products/${id}`);
  },

  async search(name: string, params: PaginatedRequest = { page: 0, size: 10 }): Promise<PaginatedResponse<Product>> {
    const { data } = await apiClient.get<PaginatedResponse<Product>>('/products/search', {
      params: { name, page: params.page, size: params.size },
    });
    return data;
  },

  async getByCategory(categoryId: string, params: PaginatedRequest = { page: 0, size: 10 }): Promise<PaginatedResponse<Product>> {
    const { data } = await apiClient.get<PaginatedResponse<Product>>(`/products/category/${categoryId}`, {
      params: { page: params.page, size: params.size },
    });
    return data;
  },

  async filter(filter: ProductFilter, params: PaginatedRequest = { page: 0, size: 10 }): Promise<PaginatedResponse<Product>> {
    const { data } = await apiClient.post<PaginatedResponse<Product>>('/products/filter', filter, {
      params: { page: params.page, size: params.size },
    });
    return data;
  },
};