import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { authInterceptor } from './interceptors/auth';
import { errorInterceptor } from './interceptors/error';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export const apiClient = axios.create({
  baseURL: API_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => errorInterceptor(error)
);

apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => authInterceptor(config),
  (error: AxiosError) => Promise.reject(error)
);

export default apiClient;