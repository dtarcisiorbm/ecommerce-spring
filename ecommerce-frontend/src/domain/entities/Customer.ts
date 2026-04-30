export interface Customer {
  id: string;
  fullName: string;
  email: string;
  taxId: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CustomerRequest {
  fullName: string;
  email: string;
  taxId: string;
  password?: string;
}