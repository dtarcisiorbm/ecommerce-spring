import { AxiosError } from 'axios';
import { ApiError } from '@/domain/types/api';

export const errorInterceptor = async (error: AxiosError): Promise<never> => {
  const originalRequest = error.config;

  if (error.response?.status === 401) {
    if (typeof window !== 'undefined') {
      localStorage.removeItem('auth_token');
      if (!originalRequest?.url?.includes('/auth/login') && !originalRequest?.url?.includes('/auth/validate')) {
        window.location.href = '/login';
      }
    }
  }

  const apiError: ApiError = {
    message: error.response?.data
      ? (error.response.data as { message?: string }).message || error.message
      : error.message,
    code: error.code,
    status: error.response?.status,
  };

  return Promise.reject(apiError);
};