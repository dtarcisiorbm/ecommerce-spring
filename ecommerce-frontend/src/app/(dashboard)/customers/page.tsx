'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { CustomerRepository } from '@/infra/repositories/CustomerRepository';
import { Button, Input, Card, CardContent, Badge, Pagination, Loading, EmptyState, Modal } from '@/presentation/components/common';
import { formatDate } from '@/shared/utils/format';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { customerSchema, type CustomerFormData } from '@/shared/utils/validation';
import { Customer } from '@/domain/entities';
import { Pencil, Trash2 } from 'lucide-react';

export default function CustomersPage() {
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCustomer, setEditingCustomer] = useState<CustomerFormData & { id?: string } | null>(null);
  const [search, setSearch] = useState('');
  const queryClient = useQueryClient();

  const { data: customers, isLoading } = useQuery({
    queryKey: ['customers', page, size],
    queryFn: () => CustomerRepository.list({ page, size }),
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: CustomerFormData }) =>
      CustomerRepository.update(id, data),
    onSuccess: () => {
      toast.success('Cliente atualizado');
      queryClient.invalidateQueries({ queryKey: ['customers'] });
    },
    onError: () => toast.error('Erro ao atualizar'),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => CustomerRepository.delete(id),
    onSuccess: () => {
      toast.success('Cliente removido');
      queryClient.invalidateQueries({ queryKey: ['customers'] });
    },
    onError: () => toast.error('Erro ao remover'),
  });

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CustomerFormData>({
    resolver: zodResolver(customerSchema),
  });

  const onSubmit = async (data: CustomerFormData) => {
    try {
      if (editingCustomer?.id) {
        await updateMutation.mutateAsync({ id: editingCustomer.id, data });
        setIsModalOpen(false);
        setEditingCustomer(null);
        reset();
      }
    } catch {
      toast.error('Erro ao salvar');
    }
  };

  const openEditModal = (customer: Customer) => {
    setEditingCustomer(customer);
    reset({
      fullName: customer.fullName,
      email: customer.email,
      taxId: customer.taxId,
    });
    setIsModalOpen(true);
  };

  if (isLoading) return <Loading fullScreen />;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Clientes</h1>
      </div>

      {customers?.content.length === 0 ? (
        <EmptyState title="Nenhum cliente encontrado" description="Clientes cadastrados aparecerão aqui" />
      ) : (
        <>
          <Card className="mb-6">
            <CardContent>
              <div className="flex gap-4">
                <div className="flex-1 relative">
                  <Input
                    placeholder="Buscar clientes..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                  />
                </div>
              </div>
            </CardContent>
          </Card>

          <div className="grid gap-4">
            {customers?.content.map((customer) => (
              <Card key={customer.id}>
                <CardContent className="flex items-center justify-between">
                  <div>
                    <h3 className="font-semibold text-gray-900 dark:text-white">{customer.fullName}</h3>
                    <p className="text-sm text-gray-500">{customer.email}</p>
                    <p className="text-sm text-gray-500">CPF: {customer.taxId}</p>
                  </div>
                  <div className="flex flex-col items-end gap-2">
                    <Badge variant={customer.active ? 'success' : 'default'}>
                      {customer.active ? 'Ativo' : 'Inativo'}
                    </Badge>
                    <p className="text-xs text-gray-500">
                      Desde {formatDate(customer.createdAt)}
                    </p>
                  </div>
                  <div className="flex gap-2 ml-4">
                    <Button variant="ghost" size="sm" onClick={() => openEditModal(customer)}>
                      <Pencil className="h-4 w-4" />
                    </Button>
                    <Button variant="danger" size="sm" onClick={() => deleteMutation.mutate(customer.id)}>
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>

          {customers && customers.totalPages > 1 && (
            <div className="mt-4 flex justify-center">
              <Pagination currentPage={page} totalPages={customers.totalPages} onPageChange={setPage} />
            </div>
          )}
        </>
      )}

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Editar Cliente">
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input label="Nome" error={errors.fullName?.message} {...register('fullName')} />
          <Input label="Email" type="email" error={errors.email?.message} {...register('email')} />
          <Input label="CPF/CNPJ" error={errors.taxId?.message} {...register('taxId')} />
          <div className="flex gap-2 justify-end">
            <Button type="button" variant="ghost" onClick={() => setIsModalOpen(false)}>
              Cancelar
            </Button>
            <Button type="submit">Salvar</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
}