import apiClient from '../api/client';
import { LoginRequest, LoginResponse, RegisterRequest, TokenValidationResponse, CustomerRegisterRequest } from '@/domain/types/api';

export const AuthRepository = {
  async login(request: LoginRequest): Promise<LoginResponse> {
    const { data } = await apiClient.post<LoginResponse>('/auth/login', request);
    return data;
  },

  async loginCustomer(request: LoginRequest): Promise<LoginResponse> {
    const { data } = await apiClient.post<LoginResponse>('/auth/customer/login', request);
    return data;
  },

  async register(request: RegisterRequest): Promise<void> {
    await apiClient.post('/auth/register', request);
  },

  async registerAdmin(request: RegisterRequest): Promise<void> {
    await apiClient.post('/auth/register/admin', request);
  },

  async registerCustomer(request: CustomerRegisterRequest): Promise<void> {
    await apiClient.post('/auth/customer/register', request);
  },

  async validateToken(token?: string): Promise<TokenValidationResponse> {
    const headers = token ? { Authorization: `Bearer ${token}` } : {};
    const { data } = await apiClient.post<TokenValidationResponse>('/auth/validate', {}, { headers });
    return data;
  },

  async refreshToken(token: string): Promise<LoginResponse> {
    const { data } = await apiClient.post<LoginResponse>('/auth/refresh', {}, {
      headers: { Authorization: `Bearer ${token}` },
    });
    return data;
  },
};