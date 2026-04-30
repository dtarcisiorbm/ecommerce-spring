'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { OrderRepository } from '@/infra/repositories/OrderRepository';
import { Button, Card, CardContent, Badge, Pagination, Loading, EmptyState, Modal, Select } from '@/presentation/components/common';
import { formatCurrency, formatDateTime } from '@/shared/utils/format';
import { Order, OrderItem, OrderStatus } from '@/domain/entities/Order';
import { Eye } from 'lucide-react';

const statusOptions = [
  { value: 'PENDING', label: 'Pendente' },
  { value: 'PAID', label: 'Pago' },
  { value: 'SHIPPED', label: 'Enviado' },
  { value: 'DELIVERED', label: 'Entregue' },
  { value: 'CANCELED', label: 'Cancelado' },
];

const statusBadge: Record<OrderStatus, 'default' | 'success' | 'warning' | 'danger' | 'info'> = {
  PENDING: 'warning',
  PAID: 'success',
  SHIPPED: 'info',
  DELIVERED: 'success',
  CANCELED: 'danger',
};

export default function OrdersPage() {
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const queryClient = useQueryClient();

  const { data: orders, isLoading } = useQuery({
    queryKey: ['orders', page, size],
    queryFn: () => OrderRepository.list({ page, size, sort: 'createdAt' }),
  });

  const updateStatusMutation = useMutation({
    mutationFn: ({ id, status }: { id: string; status: OrderStatus }) =>
      OrderRepository.updateStatus(id, status),
    onSuccess: () => {
      toast.success('Status atualizado');
      queryClient.invalidateQueries({ queryKey: ['orders'] });
    },
    onError: () => toast.error('Erro ao atualizar status'),
  });

  const openOrderModal = (order: Order) => {
    setSelectedOrder(order);
    setIsModalOpen(true);
  };

  const handleStatusChange = async (orderId: string, status: OrderStatus) => {
    await updateStatusMutation.mutateAsync({ id: orderId, status });
  };

  if (isLoading) return <Loading fullScreen />;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Pedidos</h1>
      </div>

      {orders?.content.length === 0 ? (
        <EmptyState title="Nenhum pedido encontrado" description="Pedidos aparecerão aqui" />
      ) : (
        <>
          <div className="grid gap-4">
            {orders?.content.map((order) => (
              <Card key={order.id}>
                <CardContent className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-gray-500">
                      #{order.id.slice(0, 8)} - {formatDateTime(order.createdAt)}
                    </p>
                    <p className="font-semibold text-gray-900 dark:text-white">
                      {order.customer?.fullName || 'Cliente não identificado'}
                    </p>
                    <p className="text-sm text-gray-500">
                      {order.items?.length || 0} item(s)
                    </p>
                  </div>
                  <div className="flex flex-col items-end gap-2">
                    <Badge variant={statusBadge[order.status]}>
                      {statusOptions.find(s => s.value === order.status)?.label || order.status}
                    </Badge>
                    <p className="text-lg font-bold text-gray-900 dark:text-white">
                      {formatCurrency(
                        order.items?.reduce(
                          (sum: number, item: OrderItem) => sum + item.unitPrice * item.quantity,
                          0
                        ) || 0
                      )}
                    </p>
                  </div>
                  <div className="flex gap-2 ml-4">
                    <Button variant="ghost" size="sm" onClick={() => openOrderModal(order)}>
                      <Eye className="h-4 w-4" />
                    </Button>
                    <Select
                      value={order.status}
                      onChange={(e) => handleStatusChange(order.id, e.target.value as OrderStatus)}
                      options={statusOptions}
                      className="w-32"
                    />
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>

          {orders && orders.totalPages > 1 && (
            <div className="mt-4 flex justify-center">
              <Pagination currentPage={page} totalPages={orders.totalPages} onPageChange={setPage} />
            </div>
          )}
        </>
      )}

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={"Pedido #".concat(selectedOrder?.id?.slice(0, 8) || '')}
        size="lg"
      >
        {selectedOrder && (
          <div className="space-y-4">
            <div>
              <h4 className="font-semibold text-gray-900 dark:text-white">Cliente</h4>
              <p className="text-gray-600 dark:text-gray-400">{selectedOrder.customer?.fullName}</p>
              <p className="text-sm text-gray-500">{selectedOrder.customer?.email}</p>
            </div>
            <div>
              <h4 className="font-semibold text-gray-900 dark:text-white">Itens</h4>
              <div className="mt-2 space-y-2">
                {selectedOrder.items?.map((item: OrderItem, idx: number) => (
                  <div key={idx} className="flex justify-between text-sm">
                    <span className="text-gray-600 dark:text-gray-400">
                      {item.quantity}x Produto #{item.productId.slice(0, 8)}
                    </span>
                    <span className="text-gray-900 dark:text-white">{formatCurrency(item.unitPrice * item.quantity)}</span>
                  </div>
                ))}
              </div>
            </div>
            <div className="border-t pt-4">
              <div className="flex justify-between">
                <span className="font-semibold text-gray-900 dark:text-white">Total</span>
                <span className="font-bold text-lg text-gray-900 dark:text-white">
                  {formatCurrency(
                    selectedOrder.items?.reduce(
                      (sum: number, item: OrderItem) => sum + item.unitPrice * item.quantity,
                      0
                    ) || 0
                  )}
                </span>
              </div>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
}