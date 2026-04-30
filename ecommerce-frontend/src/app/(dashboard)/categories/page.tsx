'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { CategoryRepository } from '@/infra/repositories/CategoryRepository';
import { Button, Input, Card, CardContent, Badge, Pagination, Loading, EmptyState, Modal } from '@/presentation/components/common';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { categorySchema, type CategoryFormData } from '@/shared/utils/validation';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import { Category } from '@/domain/entities';

export default function CategoriesPage() {
  const [page, setPage] = useState(0);
  const [size] = useState(20);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCategory, setEditingCategory] = useState<CategoryFormData & { id?: string } | null>(null);
  const queryClient = useQueryClient();

  const { data: categories, isLoading } = useQuery({
    queryKey: ['categories', page, size],
    queryFn: () => CategoryRepository.list({ page, size }),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => CategoryRepository.delete(id),
    onSuccess: () => {
      toast.success('Categoria excluída');
      queryClient.invalidateQueries({ queryKey: ['categories'] });
    },
    onError: () => toast.error('Erro ao excluir'),
  });

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CategoryFormData>({
    resolver: zodResolver(categorySchema),
  });

  const onSubmit = async (data: CategoryFormData) => {
    try {
      const categoryData = {
        name: data.name,
        description: data.description || undefined,
        parentId: data.parentId || undefined,
      };
      if (editingCategory?.id) {
        await CategoryRepository.update(editingCategory.id, categoryData);
        toast.success('Categoria atualizada');
      } else {
        await CategoryRepository.create(categoryData);
        toast.success('Categoria criada');
      }
      setIsModalOpen(false);
      setEditingCategory(null);
      reset();
      queryClient.invalidateQueries({ queryKey: ['categories'] });
    } catch {
      toast.error('Erro ao salvar');
    }
  };

  const openEditModal = (category: Category) => {
    const { id, name, description, parentId } = category;
    setEditingCategory({ id, name, description: description || undefined, parentId: parentId || undefined });
    reset({
      name: category.name,
      description: category.description || '',
      parentId: category.parentId || '',
    });
    setIsModalOpen(true);
  };

  const openCreateModal = () => {
    setEditingCategory(null);
    reset();
    setIsModalOpen(true);
  };

  if (isLoading) return <Loading fullScreen />;

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Categorias</h1>
        <Button onClick={openCreateModal}>
          <Plus className="h-4 w-4 mr-2" /> Nova Categoria
        </Button>
      </div>

      {categories?.content.length === 0 ? (
        <EmptyState
          title="Nenhuma categoria encontrada"
          description="Comece cadastrando suas categorias"
          action={<Button onClick={openCreateModal}><Plus className="h-4 w-4 mr-2" />Nova Categoria</Button>}
        />
      ) : (
        <>
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {categories?.content.map((category) => (
              <Card key={category.id}>
                <CardContent className="flex items-center justify-between">
                  <div>
                    <h3 className="font-semibold text-gray-900 dark:text-white">{category.name}</h3>
                    <p className="text-sm text-gray-500">{category.description || 'Sem descrição'}</p>
                    <Badge variant={category.active ? 'success' : 'default'} className="mt-2">
                      {category.active ? 'Ativa' : 'Inativa'}
                    </Badge>
                  </div>
                  <div className="flex gap-2">
                    <Button variant="ghost" size="sm" onClick={() => openEditModal(category)}>
                      <Pencil className="h-4 w-4" />
                    </Button>
                    <Button variant="danger" size="sm" onClick={() => deleteMutation.mutate(category.id)}>
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>

          {categories && categories.totalPages > 1 && (
            <div className="mt-4 flex justify-center">
              <Pagination currentPage={page} totalPages={categories.totalPages} onPageChange={setPage} />
            </div>
          )}
        </>
      )}

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title={editingCategory?.id ? 'Editar Categoria' : 'Nova Categoria'}>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input label="Nome" error={errors.name?.message} {...register('name')} />
          <Input label="Descrição" error={errors.description?.message} {...register('description')} />
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