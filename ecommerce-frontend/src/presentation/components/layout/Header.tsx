'use client';

import { ReactNode } from 'react';
import { clsx } from 'clsx';
import { Menu, X, ShoppingCart, User, Search } from 'lucide-react';
import { Button } from '@/presentation/components/common';

interface HeaderProps {
  title?: string;
  subtitle?: string;
  leftAction?: ReactNode;
  rightAction?: ReactNode;
  onMenuToggle?: () => void;
  isMenuOpen?: boolean;
  showCart?: boolean;
  cartItemCount?: number;
  transparent?: boolean;
}

export function Header({
  title,
  subtitle,
  leftAction,
  rightAction,
  onMenuToggle,
  isMenuOpen = false,
  showCart = false,
  cartItemCount = 0,
  transparent = false,
}: HeaderProps) {
  return (
    <header 
      className={clsx(
        'sticky top-0 z-50 w-full transition-all duration-200',
        transparent 
          ? 'bg-transparent backdrop-blur-sm border-b border-white/10' 
          : 'bg-white dark:bg-gray-900 border-b border-gray-200 dark:border-gray-700'
      )}
    >
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16 min-h-[4rem]">
          {/* Left Section */}
          <div className="flex items-center gap-3">
            {onMenuToggle && (
              <Button
                variant="ghost"
                size="sm"
                onClick={onMenuToggle}
                className="lg:hidden"
                aria-label="Toggle menu"
              >
                {isMenuOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
              </Button>
            )}
            
            {leftAction}
            
            {(title || subtitle) && (
              <div className="flex flex-col">
                {title && (
                  <h1 className="text-lg font-semibold text-gray-900 dark:text-white leading-tight">
                    {title}
                  </h1>
                )}
                {subtitle && (
                  <p className="text-sm text-gray-500 dark:text-gray-400">
                    {subtitle}
                  </p>
                )}
              </div>
            )}
          </div>

          {/* Right Section */}
          <div className="flex items-center gap-2">
            {rightAction}
            
            {showCart && (
              <Button variant="ghost" size="sm" className="relative" aria-label="Shopping cart">
                <ShoppingCart className="h-5 w-5" />
                {cartItemCount > 0 && (
                  <span className="absolute -top-1 -right-1 h-5 w-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center">
                    {cartItemCount > 99 ? '99+' : cartItemCount}
                  </span>
                )}
              </Button>
            )}
            
            <Button variant="ghost" size="sm" className="hidden sm:flex" aria-label="User account">
              <User className="h-5 w-5" />
            </Button>
          </div>
        </div>
      </div>
    </header>
  );
}

interface ShopHeaderProps {
  onMenuToggle?: () => void;
  isMenuOpen?: boolean;
  cartItemCount?: number;
}

export function ShopHeader({ onMenuToggle, isMenuOpen, cartItemCount = 0 }: ShopHeaderProps) {
  return (
    <Header
      title="Minha Loja"
      showCart={true}
      cartItemCount={cartItemCount}
      onMenuToggle={onMenuToggle}
      isMenuOpen={isMenuOpen}
    />
  );
}

interface DashboardHeaderProps {
  userName?: string;
  onMenuToggle?: () => void;
  isMenuOpen?: boolean;
}

export function DashboardHeader({ userName, onMenuToggle, isMenuOpen }: DashboardHeaderProps) {
  return (
    <Header
      title="Painel"
      subtitle={`Olá, ${userName || 'Admin'}`}
      onMenuToggle={onMenuToggle}
      isMenuOpen={isMenuOpen}
    />
  );
}
