import { z } from 'zod';

export const loginSchema = z.object({
  email: z.string().min(1, 'Email é obrigatório').email('Email inválido'),
  password: z.string().min(1, 'Senha é obrigatória').min(6, 'Senha deve ter pelo menos 6 caracteres'),
});

export const registerSchema = z.object({
  name: z.string().min(1, 'Nome é obrigatório').max(100, 'Nome muito longo'),
  email: z.string().min(1, 'Email é obrigatório').email('Email inválido'),
  password: z.string().min(1, 'Senha é obrigatória').min(6, 'Senha deve ter pelo menos 6 caracteres'),
  confirmPassword: z.string().min(1, 'Confirmação de senha é obrigatória'),
}).refine((data) => data.password === data.confirmPassword, {
  message: 'As senhas não conferem',
  path: ['confirmPassword'],
});

export const productSchema = z.object({
  name: z.string().min(1, 'Nome é obrigatório').max(200, 'Nome muito longo'),
  sku: z.string().min(1, 'SKU é obrigatório').max(50, 'SKU muito longo'),
  price: z.string().transform((val) => Number(val)),
  stock: z.string().transform((val) => Number(val)),
  categoryId: z.string().optional(),
});

export type ProductFormData = z.infer<typeof productSchema>;

export const categorySchema = z.object({
  name: z.string().min(1, 'Nome é obrigatório').max(100, 'Nome muito longo'),
  description: z.string().optional(),
  parentId: z.string().optional(),
});

export interface CategoryFormData {
  name: string;
  description?: string;
  parentId?: string;
}

export const customerSchema = z.object({
  fullName: z.string().min(1, 'Nome completo é obrigatório').max(200, 'Nome muito longo'),
  email: z.string().min(1, 'Email é obrigatório').email('Email inválido'),
  taxId: z.string().min(1, 'CPF/CNPJ é obrigatório'),
  password: z.string().optional(),
});

export interface CustomerFormData {
  fullName: string;
  email: string;
  taxId: string;
  password?: string;
}

export const orderItemSchema = z.object({
  productId: z.string(),
  quantity: z.coerce.number().int().min(1, 'Quantidade mínima é 1'),
  unitPrice: z.coerce.number().min(0, 'Preço deve ser positivo'),
});

export const orderSchema = z.object({
  customerId: z.string(),
  items: z.array(orderItemSchema),
});

export interface OrderFormData {
  customerId: string;
  items: Array<{
    productId: string;
    quantity: number;
    unitPrice: number;
  }>;
}

export type LoginFormData = z.infer<typeof loginSchema>;
export type RegisterFormData = z.infer<typeof registerSchema>;