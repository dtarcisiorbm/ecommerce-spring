'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { ProductRepository } from '@/infra/repositories/ProductRepository';
import { CategoryRepository } from '@/infra/repositories/CategoryRepository';
import { Button, Input, Select, Card, CardContent, Badge, Pagination, Loading, EmptyState, Modal } from '@/presentation/components/common';
import { formatCurrency } from '@/shared/utils/format';
import { Product } from '@/domain/entities';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { productSchema, type ProductFormData } from '@/shared/utils/validation';
import { Plus, Pencil, Trash2 } from 'lucide-react';

export default function ProductsPage() {
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [search, setSearch] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState<ProductFormData & { id?: string } | null>(null);
  const queryClient = useQueryClient();

  const { data: products, isLoading } = useQuery({
    queryKey: ['products', page, size, search],
    queryFn: () => search
      ? ProductRepository.search(search, { page, size })
      : ProductRepository.list({ page, size }),
  });

  const { data: categories } = useQuery({
    queryKey: ['categories-active'],
    queryFn: () => CategoryRepository.listActive(),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => ProductRepository.delete(id),
    onSuccess: () => {
      toast.success('Produto excluído');
      queryClient.invalidateQueries({ queryKey: ['products'] });
    },
    onError: () => toast.error('Erro ao excluir'),
  });

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(productSchema),
  });

  const onSubmit = async (data: ProductFormData) => {
    try {
      const productData = {
        name: data.name,
        sku: data.sku,
        price: data.price,
        stock: data.stock,
        categoryId: data.categoryId || undefined,
      };
      if (editingProduct?.id) {
        await ProductRepository.update(editingProduct.id, productData);
        toast.success('Produto atualizado');
      } else {
        await ProductRepository.create(productData);
        toast.success('Produto criado');
      }
      setIsModalOpen(false);
      setEditingProduct(null);
      reset();
      queryClient.invalidateQueries({ queryKey: ['products'] });
    } catch {
      toast.error('Erro ao salvar');
    }
  };

  const openEditModal = (product: Product) => {
    const { id, name, sku, price, stock, categoryId } = product;
    setEditingProduct({ id, name, sku, price, stock, categoryId: categoryId || undefined });
    reset({
      name: product.name,
      sku: product.sku,
      price: String(product.price),
      stock: String(product.stock),
      categoryId: product.categoryId || '',
    });
    setIsModalOpen(true);
  };

  const openCreateModal = () => {
    setEditingProduct(null);
    reset();
    setIsModalOpen(true);
  };

  if (isLoading) return <Loading fullScreen />;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Produtos</h1>
        <Button onClick={openCreateModal}>
          <Plus className="h-4 w-4 mr-2" /> Novo Produto
        </Button>
      </div>

      <Card className="mb-6">
        <CardContent>
          <div className="flex gap-4">
            <div className="flex-1">
              <Input
                placeholder="Buscar produtos..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
            </div>
            <Select
              options={[
                { value: '', label: 'Todas as categorias' },
                ...(categories?.map((c) => ({ value: c.id, label: c.name })) || []),
              ]}
            />
          </div>
        </CardContent>
      </Card>

      {products?.content.length === 0 ? (
        <EmptyState
          title="Nenhum produto encontrado"
          description="Comece cadastrando seus produtos"
          action={<Button onClick={openCreateModal}><Plus className="h-4 w-4 mr-2" />Novo Produto</Button>}
        />
      ) : (
        <>
          <div className="grid gap-4">
            {products?.content.map((product) => (
              <Card key={product.id} className="flex items-center justify-between">
                <div className="flex-1">
                  <h3 className="font-semibold text-gray-900 dark:text-white">{product.name}</h3>
                  <p className="text-sm text-gray-500">SKU: {product.sku}</p>
                </div>
                <div className="text-right">
                  <p className="font-semibold text-gray-900 dark:text-white">
                    {formatCurrency(product.price)}
                  </p>
                  <Badge variant={product.stock > 0 ? 'success' : 'danger'}>
                    {product.stock} em estoque
                  </Badge>
                </div>
                <div className="flex gap-2 ml-4">
                  <Button variant="ghost" size="sm" onClick={() => openEditModal(product)}>
                    <Pencil className="h-4 w-4" />
                  </Button>
                  <Button variant="danger" size="sm" onClick={() => deleteMutation.mutate(product.id)}>
                    <Trash2 className="h-4 w-4" />
                  </Button>
                </div>
              </Card>
            ))}
          </div>

          {products && (
            <div className="mt-4 flex justify-center">
              <Pagination
                currentPage={page}
                totalPages={products.totalPages}
                onPageChange={setPage}
              />
            </div>
          )}
        </>
      )}

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={editingProduct?.id ? 'Editar Produto' : 'Novo Produto'}>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input label="Nome" error={errors.name?.message} {...register('name')} />
          <Input label="SKU" error={errors.sku?.message} {...register('sku')} />
          <Input label="Preço" type="number" step="0.01" error={errors.price?.message} {...register('price', { valueAsNumber: true })} />
          <Input label="Estoque" type="number" error={errors.stock?.message} {...register('stock', { valueAsNumber: true })} />
          <Select
            label="Categoria"
            options={[
              { value: '', label: 'Selecione uma categoria' },
              ...(categories?.map((c) => ({ value: c.id, label: c.name })) || []),
            ]}
            {...register('categoryId')}
          />
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