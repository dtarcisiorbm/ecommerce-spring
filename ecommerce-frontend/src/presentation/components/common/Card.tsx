'use client';

import { ReactNode } from 'react';
import { clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

interface CardProps {
  children: ReactNode;
  className?: string;
  hover?: boolean;
  padding?: 'none' | 'sm' | 'md' | 'lg' | 'xl';
  elevated?: boolean;
}

export function Card({ children, className, hover = false, padding = 'md', elevated = false }: CardProps) {
  const paddingStyles = {
    none: '',
    sm: 'p-4',
    md: 'p-6',
    lg: 'p-8',
    xl: 'p-10',
  };

  return (
    <div
      className={twMerge(
        clsx(
          'bg-white dark:bg-gray-800 rounded-2xl border border-gray-200 dark:border-gray-700 transition-all duration-200',
          elevated ? 'shadow-lg' : 'shadow-sm',
          hover && 'hover:shadow-xl hover:border-gray-300 dark:hover:border-gray-600 hover:-translate-y-1',
          paddingStyles[padding],
          className
        )
      )}
    >
      {children}
    </div>
  );
}

export function CardHeader({ children, className }: { children: ReactNode; className?: string }) {
  return (
    <div className={clsx('border-b border-gray-200 dark:border-gray-700 pb-4 mb-4', className)}>
      {children}
    </div>
  );
}

export function CardTitle({ children, className }: { children: ReactNode; className?: string }) {
  return (
    <h3 className={clsx('text-xl font-semibold text-gray-900 dark:text-white leading-tight', className)}>
      {children}
    </h3>
  );
}

export function CardSubtitle({ children, className }: { children: ReactNode; className?: string }) {
  return (
    <p className={clsx('text-sm text-gray-600 dark:text-gray-400 mt-1', className)}>
      {children}
    </p>
  );
}

export function CardContent({ children, className }: { children: ReactNode; className?: string }) {
  return <div className={className}>{children}</div>;
}

export function CardFooter({ children, className }: { children: ReactNode; className?: string }) {
  return (
    <div className={clsx('border-t border-gray-200 dark:border-gray-700 pt-4 mt-4', className)}>
      {children}
    </div>
  );
}