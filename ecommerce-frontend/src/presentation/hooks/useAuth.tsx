'use client';

import { createContext, useContext, useState, useEffect, useCallback, ReactNode } from 'react';
import { AuthRepository } from '@/infra/repositories/AuthRepository';
import { AuthUser, UserRole } from '@/domain/types/api';

interface AuthContextType {
  user: AuthUser | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  loginCustomer: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [isLoading, setIsLoading] = useState(() => {
    if (typeof window === 'undefined') return false;
    const token = localStorage.getItem('auth_token');
    return !!token;
  });

  const logout = useCallback(() => {
    if (typeof window !== 'undefined') {
      localStorage.removeItem('auth_token');
      document.cookie = 'auth_token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT';
    }
    setUser(null);
  }, []);

  const parseJwtPayload = (token: string): { sub?: string; roles?: string[] } => {
    try {
      const payload = token.split('.')[1];
      const padding = 4 - (payload.length % 4);
      const padded = padding !== 4 ? payload + '='.repeat(padding) : payload;
      return JSON.parse(atob(padded));
    } catch {
      return {};
    }
  };

  const validateAndSetUser = useCallback(async (token: string) => {
    try {
      const response = await AuthRepository.validateToken(token);
      if (response.valid && response.subject) {
        const jwtPayload = parseJwtPayload(token);
        const email = response.subject;
        const roles: string[] = jwtPayload.roles ?? [];

        // Determine role: prefer ADMIN > CUSTOMER > USER
        let role: UserRole = 'USER';
        if (roles.includes('ADMIN')) role = 'ADMIN';
        else if (roles.includes('CUSTOMER')) role = 'CUSTOMER';

        setUser({
          id: email, // backend uses email as subject/id
          email,
          name: email.split('@')[0],
          role,
        });
      } else {
        logout();
      }
    } catch {
      logout();
    } finally {
      setIsLoading(false);
    }
  }, [logout]);

  useEffect(() => {
    if (typeof window === 'undefined') return;
    
    const token = localStorage.getItem('auth_token');
    if (token && !user) {
      validateAndSetUser(token);
    } else if (!token) {
      setIsLoading(false);
    }
  }, [validateAndSetUser, user]);

  const login = async (email: string, password: string) => {
    const response = await AuthRepository.login({ email, password });
    if (typeof window !== 'undefined') {
      localStorage.setItem('auth_token', response.token);
      document.cookie = `auth_token=${response.token}; path=/; max-age=86400; SameSite=Lax`;
    }
    await validateAndSetUser(response.token);
  };

  const loginCustomer = async (email: string, password: string) => {
    const response = await AuthRepository.loginCustomer({ email, password });
    if (typeof window !== 'undefined') {
      localStorage.setItem('auth_token', response.token);
      document.cookie = `auth_token=${response.token}; path=/; max-age=86400; SameSite=Lax`;
    }
    await validateAndSetUser(response.token);
  };

  const value = { user, isLoading, isAuthenticated: !!user, login, loginCustomer, logout };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};