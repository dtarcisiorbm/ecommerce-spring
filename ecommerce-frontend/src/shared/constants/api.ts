export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/auth/login',
    LOGIN_CUSTOMER: '/auth/customer/login',
    REGISTER: '/auth/register',
    REGISTER_ADMIN: '/auth/register/admin',
    REGISTER_CUSTOMER: '/auth/customer/register',
    VALIDATE: '/auth/validate',
    REFRESH: '/auth/refresh',
  },
  PRODUCTS: {
    BASE: '/products',
    SEARCH: '/products/search',
    FILTER: '/products/filter',
    BY_CATEGORY: (id: string) => `/products/category/${id}`,
  },
  CATEGORIES: {
    BASE: '/categories',
    ACTIVE: '/categories/active',
    ROOT: '/categories/root',
    SUBCATEGORIES: (id: string) => `/categories/${id}/subcategories`,
  },
  CUSTOMERS: {
    BASE: '/customers',
    ACTIVE: '/customers/active',
  },
  ORDERS: {
    BASE: '/orders',
    STATUS: (id: string) => `/orders/${id}/status`,
  },
  CART: {
    BASE: '/cart',
    ITEMS: '/cart/items',
    ITEM: (id: string) => `/cart/items/${id}`,
  },
  PAYMENTS: {
    BASE: '/payments',
    PROCESS: '/payments/process',
    BY_ORDER: (id: string) => `/payments/order/${id}`,
    REFUND: (id: string) => `/payments/${id}/refund`,
    REFUND_FULL: (id: string) => `/payments/${id}/refund/full`,
  },
} as const;

export const API_METHODS = {
  GET: 'GET',
  POST: 'POST',
  PUT: 'PUT',
  PATCH: 'PATCH',
  DELETE: 'DELETE',
} as const;

export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  NO_CONTENT: 204,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  INTERNAL_SERVER_ERROR: 500,
} as const;