'use client';

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { CartRepository } from '@/infra/repositories/CartRepository';
import { Button, Card, CardContent, Loading } from '@/presentation/components/common';
import { formatCurrency } from '@/shared/utils/format';
import { Minus, Plus, Trash2, ShoppingCart } from 'lucide-react';
import Link from 'next/link';

export default function CartPage() {
  const queryClient = useQueryClient();

  const { data: cart, isLoading } = useQuery({
    queryKey: ['cart'],
    queryFn: () => CartRepository.getCart(),
  });

  const updateItemMutation = useMutation({
    mutationFn: ({ itemId, quantity }: { itemId: string; quantity: number }) =>
      CartRepository.updateItem(itemId, { quantity }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
    },
  });

  const removeItemMutation = useMutation({
    mutationFn: (itemId: string) => CartRepository.removeItem(itemId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
    },
  });

  const clearCartMutation = useMutation({
    mutationFn: () => CartRepository.clearCart(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cart'] });
    },
  });

  if (isLoading) return <Loading fullScreen />;

  if (!cart || cart.items.length === 0) {
    return (
      <div className="min-h-screen bg-gray-50">
        <header className="bg-white border-b">
          <div className="max-w-7xl mx-auto px-4 py-4">
            <div className="flex items-center justify-between gap-4">
              <Link href="/shop/products" className="text-2xl font-bold text-gray-900">
                Minha Loja
              </Link>
              <Link href="/shop/products">
                <Button variant="ghost">
                  <ShoppingCart className="h-5 w-5" />
                </Button>
              </Link>
            </div>
          </div>
        </header>

        <main className="max-w-7xl mx-auto px-4 py-8">
          <div className="text-center py-12">
            <ShoppingCart className="h-16 w-16 text-gray-400 mx-auto mb-4" />
            <h2 className="text-2xl font-semibold text-gray-900 mb-2">Seu carrinho está vazio</h2>
            <p className="text-gray-600 mb-6">Adicione produtos para continuar comprando</p>
            <Link href="/shop/products">
              <Button>Continuar Comprando</Button>
            </Link>
          </div>
        </main>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4 py-4">
          <div className="flex items-center justify-between gap-4">
            <Link href="/shop/products" className="text-2xl font-bold text-gray-900">
              Minha Loja
            </Link>
            <Link href="/shop/products">
              <Button variant="ghost">
                <ShoppingCart className="h-5 w-5" />
              </Button>
            </Link>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-6">Meu Carrinho</h1>

        <div className="grid gap-8 lg:grid-cols-3">
          <div className="lg:col-span-2">
            <Card>
              <CardContent className="p-6">
                <div className="space-y-4">
                  {cart.items.map((item) => (
                    <div key={item.id} className="flex items-center gap-4 p-4 border rounded-lg">
                      <div className="aspect-square w-20 h-20 bg-gray-100 rounded-lg flex items-center justify-center">
                        <ShoppingCart className="h-8 w-8 text-gray-400" />
                      </div>
                      
                      <div className="flex-1">
                        <h3 className="font-semibold text-gray-900">Produto ID: {item.productId}</h3>
                        <p className="text-sm text-gray-500">Quantidade: {item.quantity}</p>
                        <p className="text-lg font-bold text-gray-900">
                          {formatCurrency(item.unitPrice)} / unidade
                        </p>
                      </div>

                      <div className="flex items-center gap-2">
                        <Button
                          size="sm"
                          variant="ghost"
                          onClick={() => updateItemMutation.mutate({
                            itemId: item.id,
                            quantity: Math.max(1, item.quantity - 1),
                          })}
                          disabled={updateItemMutation.isPending}
                        >
                          <Minus className="h-4 w-4" />
                        </Button>
                        
                        <span className="w-8 text-center">{item.quantity}</span>
                        
                        <Button
                          size="sm"
                          variant="ghost"
                          onClick={() => updateItemMutation.mutate({
                            itemId: item.id,
                            quantity: item.quantity + 1,
                          })}
                          disabled={updateItemMutation.isPending}
                        >
                          <Plus className="h-4 w-4" />
                        </Button>
                      </div>

                      <div className="text-right">
                        <p className="font-semibold text-gray-900">
                          {formatCurrency(item.totalPrice)}
                        </p>
                        <Button
                          size="sm"
                          variant="ghost"
                          onClick={() => removeItemMutation.mutate(item.id)}
                          disabled={removeItemMutation.isPending}
                          className="text-red-500 hover:text-red-700"
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </div>
                    </div>
                  ))}
                </div>

                <div className="mt-6 flex justify-between items-center">
                  <Button
                    variant="ghost"
                    onClick={() => clearCartMutation.mutate()}
                    disabled={clearCartMutation.isPending}
                    className="text-red-500 hover:text-red-700"
                  >
                    Limpar Carrinho
                  </Button>
                  
                  <Link href="/shop/products">
                    <Button variant="ghost">Continuar Comprando</Button>
                  </Link>
                </div>
              </CardContent>
            </Card>
          </div>

          <div>
            <Card>
              <CardContent className="p-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-4">Resumo do Pedido</h2>
                
                <div className="space-y-2 mb-4">
                  <div className="flex justify-between">
                    <span>Total de Itens</span>
                    <span>{cart.totalItems}</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Frete</span>
                    <span>Grátis</span>
                  </div>
                  <div className="flex justify-between font-semibold text-lg">
                    <span>Total</span>
                    <span>{formatCurrency(cart.totalAmount)}</span>
                  </div>
                </div>

                <Link href="/shop/checkout">
                  <Button className="w-full">Finalizar Compra</Button>
                </Link>
              </CardContent>
            </Card>
          </div>
        </div>
      </main>
    </div>
  );
}
