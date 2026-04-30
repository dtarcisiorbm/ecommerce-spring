import apiClient from '../api/client';
import { CartResponse, AddCartItemRequest, UpdateCartItemRequest, ShoppingCartItem } from '@/domain/entities/ShoppingCart';

export const CartRepository = {
  async getCart(): Promise<CartResponse> {
    const { data } = await apiClient.get<CartResponse>('/cart');
    return data;
  },

  async addItem(request: AddCartItemRequest): Promise<ShoppingCartItem> {
    const { data } = await apiClient.post<ShoppingCartItem>('/cart/items', request);
    return data;
  },

  async updateItem(itemId: string, request: UpdateCartItemRequest): Promise<ShoppingCartItem> {
    const { data } = await apiClient.put<ShoppingCartItem>(`/cart/items/${itemId}`, request);
    return data;
  },

  async removeItem(itemId: string): Promise<void> {
    await apiClient.delete(`/cart/items/${itemId}`);
  },

  async clearCart(): Promise<void> {
    await apiClient.delete('/cart');
  },
};