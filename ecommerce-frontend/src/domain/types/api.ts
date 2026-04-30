export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  roles?: string[];
}

export interface CustomerRegisterRequest {
  fullName: string;
  email: string;
  taxId: string;
  password: string;
}

export interface TokenValidationResponse {
  valid: boolean;
  subject: string;
}

export type UserRole = 'ADMIN' | 'CUSTOMER' | 'USER';

export interface AuthUser {
  id: string;
  email: string;
  name: string;
  role: UserRole;
}

export interface ApiError {
  message: string;
  code?: string;
  status?: number;
}

export interface PaginatedRequest {
  page: number;
  size: number;
  sort?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}