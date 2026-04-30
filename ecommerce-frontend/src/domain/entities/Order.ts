import { Customer } from './Customer';

export interface Order {
  id: string;
  customer: Customer;
  items: OrderItem[];
  createdAt: string;
  status: OrderStatus;
}

export interface OrderItem {
  id: string;
  productId: string;
  quantity: number;
  unitPrice: number;
}

export type OrderStatus = 'PENDING' | 'PAID' | 'SHIPPED' | 'DELIVERED' | 'CANCELED';

export interface OrderRequest {
  customerId: string;
  items: OrderItemRequest[];
}

export interface OrderItemRequest {
  productId: string;
  quantity: number;
  unitPrice: number;
}

export interface OrderStatusUpdateRequest {
  status: OrderStatus;
}