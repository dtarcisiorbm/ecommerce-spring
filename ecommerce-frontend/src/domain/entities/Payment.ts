export interface Payment {
  id: string;
  orderId: string;
  amount: number;
  method: PaymentMethod;
  status: PaymentStatus;
  transactionId: string | null;
  gatewayResponse: string | null;
  createdAt: string;
  processedAt: string | null;
}

export type PaymentMethod = 'CREDIT_CARD' | 'DEBIT_CARD' | 'PIX' | 'BOLETO';

export type PaymentStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED' | 'CANCELED' | 'REFUNDED' | 'PARTIALLY_REFUNDED';

export interface PaymentRequest {
  orderId: string;
  method: PaymentMethod;
  cardNumber: string;
  cardHolderName: string;
  expiryDate: string;
  cvv: string;
  email: string;
  billingAddress?: string;
  postalCode?: string;
}

export interface RefundRequest {
  amount: number;
}