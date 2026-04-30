'use client';

import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useParams, useRouter } from 'next/navigation';
import Link from 'next/link';
import { ProductRepository } from '@/infra/repositories/ProductRepository';
import { Button, Card, CardContent, Loading } from '@/presentation/components/common';
import { formatCurrency } from '@/shared/utils/format';
import { ArrowLeft, ShoppingCart } from 'lucide-react';

export default function ProductDetailPage() {
  const params = useParams();
  const router = useRouter();
  const productId = params.id as string;
  const [quantity, setQuantity] = useState(1);

  const { data: product, isLoading } = useQuery({
    queryKey: ['product', productId],
    queryFn: () => ProductRepository.getById(productId),
    enabled: !!productId,
  });

  if (isLoading) return <Loading fullScreen />;

  if (!product) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-gray-900 mb-4">Produto não encontrado</h1>
          <Link href="/products">
            <Button>Voltar para produtos</Button>
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-4xl mx-auto px-4 py-8">
        <Link href="/products" className="inline-flex items-center text-gray-600 hover:text-gray-900 mb-6">
          <ArrowLeft className="h-4 w-4 mr-2" />
          Voltar
        </Link>

        <div className="grid md:grid-cols-2 gap-8">
          <div className="aspect-square bg-gray-200 rounded-lg flex items-center justify-center">
            <ShoppingCart className="h-24 w-24 text-gray-400" />
          </div>

          <div>
            <h1 className="text-3xl font-bold text-gray-900 mb-2">{product.name}</h1>
            <p className="text-sm text-gray-500 mb-4">SKU: {product.sku}</p>
            
            <p className="text-3xl font-bold text-gray-900 mb-6">
              {formatCurrency(product.price)}
            </p>

            <div className="mb-6">
              <p className="text-sm text-gray-600 mb-2">Quantidade:</p>
              <div className="flex items-center gap-4">
                <Button 
                  variant="outline" 
                  onClick={() => setQuantity(Math.max(1, quantity - 1))}
                >
                  -
                </Button>
                <span className="text-xl font-semibold">{quantity}</span>
                <Button 
                  variant="outline" 
                  onClick={() => setQuantity(Math.min(product.stock, quantity + 1))}
                >
                  +
                </Button>
              </div>
              <p className="text-sm text-gray-500 mt-2">
                {product.stock} disponíveis em estoque
              </p>
            </div>

            <Button className="w-full" size="lg">
              <ShoppingCart className="h-5 w-5 mr-2" />
              Adicionar ao Carrinho
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}