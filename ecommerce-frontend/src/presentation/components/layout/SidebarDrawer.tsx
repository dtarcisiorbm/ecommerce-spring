'use client';

import { ReactNode } from 'react';
import { clsx } from 'clsx';
import { X } from 'lucide-react';
import { Button } from '@/presentation/components/common';

interface SidebarDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  children: ReactNode;
  position?: 'left' | 'right';
  overlay?: boolean;
}

export function SidebarDrawer({ 
  isOpen, 
  onClose, 
  children, 
  position = 'left',
  overlay = true 
}: SidebarDrawerProps) {
  return (
    <>
      {/* Overlay */}
      {overlay && isOpen && (
        <div
          className="fixed inset-0 bg-black/50 z-40 lg:hidden transition-opacity duration-200"
          onClick={onClose}
          aria-hidden="true"
        />
      )}
      
      {/* Drawer */}
      <div
        className={clsx(
          'fixed top-0 h-full w-80 max-w-[85vw] bg-white dark:bg-gray-800 border-r border-gray-200 dark:border-gray-700 z-50 transition-transform duration-300 ease-in-out',
          position === 'left' ? 'left-0' : 'right-0',
          isOpen ? 'translate-x-0' : (position === 'left' ? '-translate-x-full' : 'translate-x-full'),
          'lg:translate-x-0 lg:static lg:z-auto',
          'lg:block lg:w-64 lg:max-w-full'
        )}
      >
        {/* Mobile Header */}
        <div className="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 lg:hidden">
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white">Menu</h2>
          <Button
            variant="ghost"
            size="sm"
            onClick={onClose}
            aria-label="Close menu"
          >
            <X className="h-5 w-5" />
          </Button>
        </div>
        
        {/* Content */}
        <div className="h-full overflow-y-auto">
          {children}
        </div>
      </div>
    </>
  );
}

interface SidebarSectionProps {
  title?: string;
  children: ReactNode;
  className?: string;
}

export function SidebarSection({ title, children, className }: SidebarSectionProps) {
  return (
    <div className={clsx('p-4', className)}>
      {title && (
        <h3 className="text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase tracking-wider mb-3">
          {title}
        </h3>
      )}
      {children}
    </div>
  );
}

interface SidebarNavItemProps {
  href: string;
  icon: React.ReactNode;
  label: string;
  isActive?: boolean;
  onClick?: () => void;
}

export function SidebarNavItem({ href, icon, label, isActive = false, onClick }: SidebarNavItemProps) {
  return (
    <a
      href={href}
      onClick={onClick}
      className={clsx(
        'flex items-center gap-3 px-3 py-2.5 rounded-xl transition-all duration-200 min-h-[2.75rem]',
        'hover:bg-gray-100 dark:hover:bg-gray-700',
        isActive
          ? 'bg-blue-50 text-blue-700 dark:bg-blue-900/20 dark:text-blue-400 font-medium'
          : 'text-gray-700 dark:text-gray-300'
      )}
    >
      <span className="h-5 w-5 flex-shrink-0">{icon}</span>
      <span className="text-sm">{label}</span>
    </a>
  );
}

interface SidebarFooterProps {
  children: ReactNode;
}

export function SidebarFooter({ children }: SidebarFooterProps) {
  return (
    <div className="absolute bottom-0 left-0 right-0 p-4 border-t border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800">
      {children}
    </div>
  );
}
