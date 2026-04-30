'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useQuery } from '@tanstack/react-query';
import { ProductRepository } from '@/infra/repositories/ProductRepository';
import { CategoryRepository } from '@/infra/repositories/CategoryRepository';
import { Button, Input, Card, CardContent, Loading, EmptyState, Select } from '@/presentation/components/common';
import { formatCurrency } from '@/shared/utils/format';
import { Search, ShoppingCart, Menu } from 'lucide-react';
import { ShopHeader } from '@/presentation/components/layout/Header';

export default function ShopPage() {
  const [page, setPage] = useState(0);
  const [size] = useState(12);
  const [search, setSearch] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const { data: products, isLoading } = useQuery({
    queryKey: ['products-shop', page, size, search, selectedCategory],
    queryFn: () => search
      ? ProductRepository.search(search, { page, size })
      : selectedCategory
        ? ProductRepository.getByCategory(selectedCategory, { page, size })
        : ProductRepository.list({ page, size }),
  });

  const { data: categories } = useQuery({
    queryKey: ['categories-active'],
    queryFn: () => CategoryRepository.listActive(),
  });

  if (isLoading) return <Loading fullScreen />;

  return (
    <div className="min-h-screen bg-gray-50">
      <ShopHeader 
        cartItemCount={0}
        onMenuToggle={() => setIsMenuOpen(!isMenuOpen)}
        isMenuOpen={isMenuOpen}
      />

      <main className="max-w-7xl mx-auto px-4 py-6 sm:py-8">
        {/* Page Header */}
        <div className="mb-6 sm:mb-8">
          <div className="flex items-center gap-3 mb-6">
            <Menu className="h-6 w-6 text-gray-600 lg:hidden" />
            <h1 className="text-2xl sm:text-3xl font-bold text-gray-900">
              Produtos
            </h1>
          </div>
          
          {/* Search and Filter */}
          <div className="flex flex-col sm:flex-row gap-3 sm:gap-4">
            <div className="flex-1">
              <Input
                placeholder="Buscar produtos..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                leftIcon={<Search className="h-5 w-5" />}
              />
            </div>
            <Select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
              options={[
                { value: '', label: 'Todas as categorias' },
                ...(categories?.map((c) => ({ value: c.id, label: c.name })) || []),
              ]}
              className="w-full sm:w-48"
            />
          </div>
        </div>

        {/* Products Grid */}
        {products?.content.length === 0 ? (
          <EmptyState
            title="Nenhum produto encontrado"
            description="Tente buscar por outro termo ou categoria"
          />
        ) : (
          <>
            <div className="grid gap-4 sm:gap-6 grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
              {products?.content.map((product) => (
                <Link key={product.id} href={`/shop/products/${product.id}`}>
                  <Card hover className="h-full">
                    <CardContent className="p-4 sm:p-6">
                      <div className="aspect-square bg-gray-100 dark:bg-gray-700 rounded-xl mb-4 flex items-center justify-center">
                        <Search className="h-12 w-12 text-gray-400" />
                      </div>
                      <h3 className="font-semibold text-gray-900 dark:text-white mb-1 line-clamp-2">
                        {product.name}
                      </h3>
                      <p className="text-sm text-gray-500 mb-2">SKU: {product.sku}</p>
                      <div className="flex items-center justify-between">
                        <span className="text-lg font-bold text-gray-900 dark:text-white">
                          {formatCurrency(product.price)}
                        </span>
                        <span className="text-sm text-gray-500">
                          {product.stock} disponíveis
                        </span>
                      </div>
                    </CardContent>
                  </Card>
                </Link>
              ))}
            </div>

            {/* Pagination */}
            {products && products.totalPages > 1 && (
              <div className="mt-8 flex justify-center gap-2 flex-wrap">
                {Array.from({ length: products.totalPages }, (_, i) => (
                  <Button
                    key={i}
                    variant={i === page ? 'primary' : 'ghost'}
                    size="sm"
                    onClick={() => setPage(i)}
                    className="min-h-[2.5rem]"
                  >
                    {i + 1}
                  </Button>
                ))}
              </div>
            )}
          </>
        )}
      </main>
    </div>
  );
}