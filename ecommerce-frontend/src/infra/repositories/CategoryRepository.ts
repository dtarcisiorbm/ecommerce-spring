import apiClient from '../api/client';
import { Category, CategoryRequest } from '@/domain/entities/Category';
import { PaginatedResponse, PaginatedRequest } from '@/domain/types/api';

export const CategoryRepository = {
  async list(params: PaginatedRequest = { page: 0, size: 20 }): Promise<PaginatedResponse<Category>> {
    const { data } = await apiClient.get<PaginatedResponse<Category>>('/categories', {
      params: { page: params.page, size: params.size },
    });
    return data;
  },

  async listActive(): Promise<Category[]> {
    const { data } = await apiClient.get<Category[]>('/categories/active');
    return data;
  },

  async listRoot(): Promise<Category[]> {
    const { data } = await apiClient.get<Category[]>('/categories/root');
    return data;
  },

  async getSubcategories(parentId: string): Promise<Category[]> {
    const { data } = await apiClient.get<Category[]>(`/categories/${parentId}/subcategories`);
    return data;
  },

  async getById(id: string): Promise<Category | null> {
    try {
      const { data } = await apiClient.get<Category>(`/categories/${id}`);
      return data;
    } catch {
      return null;
    }
  },

  async create(category: CategoryRequest): Promise<Category> {
    const { data } = await apiClient.post<Category>('/categories', category);
    return data;
  },

  async update(id: string, category: CategoryRequest): Promise<Category> {
    const { data } = await apiClient.put<Category>(`/categories/${id}`, category);
    return data;
  },

  async delete(id: string): Promise<void> {
    await apiClient.delete(`/categories/${id}`);
  },
};