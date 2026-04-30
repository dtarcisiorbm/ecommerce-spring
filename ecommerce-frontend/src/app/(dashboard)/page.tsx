'use client';

import { useQuery } from '@tanstack/react-query';
import { Loading } from '@/presentation/components/common';
import { ProductRepository } from '@/infra/repositories/ProductRepository';
import { OrderRepository } from '@/infra/repositories/OrderRepository';
import { CustomerRepository } from '@/infra/repositories/CustomerRepository';
import { Package, Users, ShoppingCart, DollarSign } from 'lucide-react';
import { Card, CardContent } from '@/presentation/components/common';

export default function DashboardPage() {
  const { data: products, isLoading: loadingProducts } = useQuery({
    queryKey: ['products', 0, 1000],
    queryFn: () => ProductRepository.list({ page: 0, size: 1000 }),
  });

  const { data: customers, isLoading: loadingCustomers } = useQuery({
    queryKey: ['customers', 0, 1000],
    queryFn: () => CustomerRepository.list({ page: 0, size: 1000 }),
  });

  const { data: orders, isLoading: loadingOrders } = useQuery({
    queryKey: ['orders', 0, 1000],
    queryFn: () => OrderRepository.list({ page: 0, size: 1000 }),
  });

  const isLoading = loadingProducts || loadingCustomers || loadingOrders;

  if (isLoading) return <Loading fullScreen />;

  const totalRevenue = orders?.content
    .filter((o) => o.status === 'PAID' || o.status === 'SHIPPED' || o.status === 'DELIVERED')
    .reduce((sum, o) => {
      return sum + o.items.reduce((s, item) => s + item.unitPrice * item.quantity, 0);
    }, 0) || 0;

  const stats = [
    { label: 'Produtos', value: products?.totalElements || 0, icon: Package, color: 'text-blue-600' },
    { label: 'Clientes', value: customers?.totalElements || 0, icon: Users, color: 'text-green-600' },
    { label: 'Pedidos', value: orders?.totalElements || 0, icon: ShoppingCart, color: 'text-purple-600' },
    { label: 'Receita', value: `R$ ${totalRevenue.toLocaleString('pt-BR')}`, icon: DollarSign, color: 'text-yellow-600', isCurrency: true },
  ];

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 dark:text-white mb-6">Painel</h1>
      
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {stats.map((stat) => (
          <Card key={stat.label}>
            <CardContent className="flex items-center gap-4">
              <div className={`p-3 rounded-lg bg-gray-100 dark:bg-gray-700 ${stat.color}`}>
                <stat.icon className="h-6 w-6" />
              </div>
              <div>
                <p className="text-sm text-gray-500 dark:text-gray-400">{stat.label}</p>
                <p className="text-2xl font-bold text-gray-900 dark:text-white">
                  {stat.isCurrency ? stat.value : stat.value.toLocaleString('pt-BR')}
                </p>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      <div className="mt-8 grid gap-4 md:grid-cols-2">
        <Card>
          <CardContent>
            <h3 className="font-semibold text-gray-900 dark:text-white mb-4">Últimos Produtos</h3>
            <div className="space-y-2">
              {products?.content.slice(0, 5).map((product) => (
                <div key={product.id} className="flex justify-between text-sm">
                  <span className="text-gray-600 dark:text-gray-400">{product.name}</span>
                  <span className="text-gray-900 dark:text-white">{product.stock}</span>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent>
            <h3 className="font-semibold text-gray-900 dark:text-white mb-4">Últimos Pedidos</h3>
            <div className="space-y-2">
              {orders?.content.slice(0, 5).map((order) => (
                <div key={order.id} className="flex justify-between text-sm">
                  <span className="text-gray-600 dark:text-gray-400">
                    {new Date(order.createdAt).toLocaleDateString('pt-BR')}
                  </span>
                  <span className={`font-medium ${
                    order.status === 'PAID' ? 'text-green-600' :
                    order.status === 'PENDING' ? 'text-yellow-600' :
                    order.status === 'CANCELED' ? 'text-red-600' : 'text-blue-600'
                  }`}>
                    {order.status}
                  </span>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}