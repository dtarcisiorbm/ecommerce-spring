import Link from 'next/link';
import { Button } from '@/presentation/components/common';
import { ShopHeader } from '@/presentation/components/layout/Header';
import { Card, CardContent } from '@/presentation/components/common';
import { Package, Users, ShoppingCart, TrendingUp, ArrowRight } from 'lucide-react';

export default function Home() {
  return (
    <div className="min-h-screen bg-gray-50">
      <ShopHeader />
      
      {/* Hero Section */}
      <section className="px-4 py-16 sm:py-20 lg:py-24">
        <div className="max-w-4xl mx-auto text-center">
          <h1 className="text-display text-gray-900 mb-6">
            Bem-vindo à Minha Loja
          </h1>
          <p className="text-subtitle text-gray-600 mb-8 max-w-2xl mx-auto">
            Sua loja virtual completa com gestão de produtos, pedidos e clientes. 
            Experiência moderna e intuitiva para você e seus clientes.
          </p>
          
          <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
            <Link href="/shop/products" className="w-full sm:w-auto">
              <Button size="lg" fullWidth className="text-base py-4">
                Ver Produtos
                <ArrowRight className="ml-2 h-5 w-5" />
              </Button>
            </Link>
            <Link href="/login" className="w-full sm:w-auto">
              <Button variant="outline" size="lg" fullWidth className="text-base py-4">
                Área Administrativa
              </Button>
            </Link>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="px-4 py-16 bg-white">
        <div className="max-w-6xl mx-auto">
          <h2 className="text-headline text-center text-gray-900 mb-12">
            Tudo que sua loja precisa
          </h2>
          
          <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-4">
            <Card hover className="text-center">
              <CardContent className="p-6">
                <div className="w-12 h-12 bg-blue-100 dark:bg-blue-900/20 rounded-xl flex items-center justify-center mx-auto mb-4">
                  <Package className="h-6 w-6 text-blue-600" />
                </div>
                <h3 className="text-title text-gray-900 mb-2">Produtos</h3>
                <p className="text-body text-gray-600">
                  Gerencie seu catálogo com facilidade
                </p>
              </CardContent>
            </Card>

            <Card hover className="text-center">
              <CardContent className="p-6">
                <div className="w-12 h-12 bg-green-100 dark:bg-green-900/20 rounded-xl flex items-center justify-center mx-auto mb-4">
                  <Users className="h-6 w-6 text-green-600" />
                </div>
                <h3 className="text-title text-gray-900 mb-2">Clientes</h3>
                <p className="text-body text-gray-600">
                  Mantenha seu cadastro sempre atualizado
                </p>
              </CardContent>
            </Card>

            <Card hover className="text-center">
              <CardContent className="p-6">
                <div className="w-12 h-12 bg-purple-100 dark:bg-purple-900/20 rounded-xl flex items-center justify-center mx-auto mb-4">
                  <ShoppingCart className="h-6 w-6 text-purple-600" />
                </div>
                <h3 className="text-title text-gray-900 mb-2">Pedidos</h3>
                <p className="text-body text-gray-600">
                  Acompanhe vendas em tempo real
                </p>
              </CardContent>
            </Card>

            <Card hover className="text-center">
              <CardContent className="p-6">
                <div className="w-12 h-12 bg-yellow-100 dark:bg-yellow-900/20 rounded-xl flex items-center justify-center mx-auto mb-4">
                  <TrendingUp className="h-6 w-6 text-yellow-600" />
                </div>
                <h3 className="text-title text-gray-900 mb-2">Analytics</h3>
                <p className="text-body text-gray-600">
                  Relatórios e insights para seu negócio
                </p>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="px-4 py-16 bg-gray-50">
        <div className="max-w-4xl mx-auto text-center">
          <h2 className="text-headline text-gray-900 mb-4">
            Pronto para começar?
          </h2>
          <p className="text-subtitle text-gray-600 mb-8">
            Explore nossa plataforma e descubra como podemos ajudar seu negócio a crescer
          </p>
          
          <Link href="/shop/products" className="inline-block">
            <Button size="lg" className="text-base py-4">
              Começar Agora
              <ArrowRight className="ml-2 h-5 w-5" />
            </Button>
          </Link>
        </div>
      </section>
    </div>
  );
}