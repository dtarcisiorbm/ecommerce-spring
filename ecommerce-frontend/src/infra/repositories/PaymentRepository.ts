import apiClient from '../api/client';
import { Payment, PaymentRequest, RefundRequest } from '@/domain/entities/Payment';

export const PaymentRepository = {
  async process(request: PaymentRequest): Promise<Payment> {
    const { data } = await apiClient.post<Payment>('/payments/process', request);
    return data;
  },

  async getById(id: string): Promise<Payment | null> {
    try {
      const { data } = await apiClient.get<Payment>(`/payments/${id}`);
      return data;
    } catch {
      return null;
    }
  },

  async getByOrder(orderId: string): Promise<Payment | null> {
    try {
      const { data } = await apiClient.get<Payment>(`/payments/order/${orderId}`);
      return data;
    } catch {
      return null;
    }
  },

  async getByStatus(status: string): Promise<Payment[]> {
    const { data } = await apiClient.get<Payment[]>(`/payments/status/${status}`);
    return data;
  },

  async refund(paymentId: string, request: RefundRequest): Promise<Payment> {
    const { data } = await apiClient.post<Payment>(`/payments/${paymentId}/refund`, request);
    return data;
  },

  async refundFull(paymentId: string): Promise<Payment> {
    const { data } = await apiClient.post<Payment>(`/payments/${paymentId}/refund/full`);
    return data;
  },
};