export interface ShoppingCart {
  id: string;
  customerId: string;
  createdAt: string;
  updatedAt: string;
}

export interface ShoppingCartItem {
  id: string;
  productId: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
}

export interface CartResponse {
  cart: ShoppingCart;
  items: ShoppingCartItem[];
  totalItems: number;
  totalAmount: number;
}

export interface AddCartItemRequest {
  productId: string;
  quantity: number;
}

export interface UpdateCartItemRequest {
  quantity: number;
}