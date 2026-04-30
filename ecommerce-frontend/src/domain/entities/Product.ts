export interface Product {
  id: string;
  name: string;
  sku: string;
  price: number;
  stock: number;
  categoryId: string | null;
}

export interface ProductFilter {
  name?: string;
  categoryId?: string;
  minPrice?: number;
  maxPrice?: number;
  inStock?: boolean;
}

export interface ProductFormData {
  name: string;
  sku: string;
  price: number;
  stock: number;
  categoryId?: string;
}

export type { PaginatedResponse } from '../types/api';
export type { PaginatedRequest } from '../types/api';