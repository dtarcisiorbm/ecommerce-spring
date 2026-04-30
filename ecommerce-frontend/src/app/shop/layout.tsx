import { ReactNode } from 'react';
import Link from 'next/link';
import { ShoppingCart } from 'lucide-react';

export default function ShopLayout({ children }: { children: ReactNode }) {
  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white border-b sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex items-center justify-between h-16">
            <Link href="/shop/products" className="text-xl font-bold text-gray-900">
              Minha Loja
            </Link>
            <div className="flex items-center gap-4">
              <Link href="/login" className="text-sm text-gray-600 hover:text-gray-900">
                Entrar
              </Link>
            </div>
          </div>
        </div>
      </header>
      {children}
    </div>
  );
}