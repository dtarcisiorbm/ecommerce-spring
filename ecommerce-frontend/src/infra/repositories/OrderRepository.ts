import apiClient from '../api/client';
import { Order, OrderRequest, OrderStatusUpdateRequest, OrderStatus } from '@/domain/entities/Order';
import { PaginatedResponse, PaginatedRequest } from '@/domain/types/api';

export const OrderRepository = {
  async list(params: PaginatedRequest = { page: 0, size: 10, sort: 'createdAt' }): Promise<PaginatedResponse<Order>> {
    const { data } = await apiClient.get<PaginatedResponse<Order>>('/orders', {
      params: { page: params.page, size: params.size, sort: params.sort },
    });
    return data;
  },

  async getById(id: string): Promise<Order | null> {
    try {
      const { data } = await apiClient.get<Order>(`/orders/${id}`);
      return data;
    } catch {
      return null;
    }
  },

  async create(order: OrderRequest): Promise<Order> {
    const { data } = await apiClient.post<Order>('/orders', order);
    return data;
  },

  async updateStatus(id: string, status: OrderStatus): Promise<Order> {
    const { data } = await apiClient.put<Order>(`/orders/${id}/status`, { status } as OrderStatusUpdateRequest);
    return data;
  },
};