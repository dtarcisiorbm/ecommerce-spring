import apiClient from '../api/client';
import { Customer, CustomerRequest } from '@/domain/entities/Customer';
import { PaginatedResponse, PaginatedRequest } from '@/domain/types/api';

export const CustomerRepository = {
  async list(params: PaginatedRequest = { page: 0, size: 10 }): Promise<PaginatedResponse<Customer>> {
    const { data } = await apiClient.get<PaginatedResponse<Customer>>('/customers', {
      params: { page: params.page, size: params.size },
    });
    return data;
  },

  async listActive(params: PaginatedRequest = { page: 0, size: 10 }): Promise<PaginatedResponse<Customer>> {
    const { data } = await apiClient.get<PaginatedResponse<Customer>>('/customers/active', {
      params: { page: params.page, size: params.size },
    });
    return data;
  },

  async getById(id: string): Promise<Customer | null> {
    try {
      const { data } = await apiClient.get<Customer>(`/customers/${id}`);
      return data;
    } catch {
      return null;
    }
  },

  async update(id: string, customer: CustomerRequest): Promise<Customer> {
    const { data } = await apiClient.put<Customer>(`/customers/${id}`, customer);
    return data;
  },

  async delete(id: string): Promise<void> {
    await apiClient.delete(`/customers/${id}`);
  },
};