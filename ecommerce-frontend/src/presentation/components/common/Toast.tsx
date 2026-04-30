'use client';

import { ReactNode } from 'react';
import { clsx } from 'clsx';
import { X, CheckCircle, AlertCircle, AlertTriangle, Info } from 'lucide-react';
import { Button } from '@/presentation/components/common';

type ToastVariant = 'success' | 'error' | 'warning' | 'info';

interface ToastProps {
  variant: ToastVariant;
  title?: string;
  description?: string;
  action?: {
    label: string;
    onClick: () => void;
  };
  onClose?: () => void;
  duration?: number;
}

const icons = {
  success: <CheckCircle className="h-5 w-5 text-green-500" />,
  error: <AlertCircle className="h-5 w-5 text-red-500" />,
  warning: <AlertTriangle className="h-5 w-5 text-yellow-500" />,
  info: <Info className="h-5 w-5 text-blue-500" />,
};

const variants = {
  success: 'bg-green-50 dark:bg-green-900/20 border-green-200 dark:border-green-800',
  error: 'bg-red-50 dark:bg-red-900/20 border-red-200 dark:border-red-800',
  warning: 'bg-yellow-50 dark:bg-yellow-900/20 border-yellow-200 dark:border-yellow-800',
  info: 'bg-blue-50 dark:bg-blue-900/20 border-blue-200 dark:border-blue-800',
};

export function Toast({ variant, title, description, action, onClose }: ToastProps) {
  return (
    <div
      className={clsx(
        'flex items-start gap-3 p-4 rounded-xl border shadow-lg transition-all duration-200 min-w-[320px] max-w-md',
        variants[variant]
      )}
    >
      <div className="flex-shrink-0 mt-0.5">
        {icons[variant]}
      </div>
      
      <div className="flex-1 min-w-0">
        {title && (
          <h4 className="text-sm font-semibold text-gray-900 dark:text-white mb-1">
            {title}
          </h4>
        )}
        {description && (
          <p className="text-sm text-gray-700 dark:text-gray-300 leading-relaxed">
            {description}
          </p>
        )}
        
        {action && (
          <div className="mt-3">
            <Button
              variant="ghost"
              size="sm"
              onClick={action.onClick}
              className="text-xs font-medium"
            >
              {action.label}
            </Button>
          </div>
        )}
      </div>
      
      {onClose && (
        <Button
          variant="ghost"
          size="sm"
          onClick={onClose}
          className="flex-shrink-0 opacity-60 hover:opacity-100"
          aria-label="Dismiss notification"
        >
          <X className="h-4 w-4" />
        </Button>
      )}
    </div>
  );
}

interface ToastContainerProps {
  children: ReactNode;
  position?: 'top-right' | 'top-left' | 'bottom-right' | 'bottom-left';
}

export function ToastContainer({ children, position = 'top-right' }: ToastContainerProps) {
  const positions = {
    'top-right': 'fixed top-4 right-4 z-50',
    'top-left': 'fixed top-4 left-4 z-50',
    'bottom-right': 'fixed bottom-4 right-4 z-50',
    'bottom-left': 'fixed bottom-4 left-4 z-50',
  };

  return (
    <div className={clsx(positions[position], 'flex flex-col gap-2 pointer-events-none')}>
      <div className="pointer-events-auto">
        {children}
      </div>
    </div>
  );
}

interface AlertBannerProps {
  variant: ToastVariant;
  title: string;
  description?: string;
  action?: {
    label: string;
    onClick: () => void;
  };
  dismissible?: boolean;
  onDismiss?: () => void;
}

export function AlertBanner({ variant, title, description, action, dismissible = false, onDismiss }: AlertBannerProps) {
  return (
    <div
      className={clsx(
        'flex items-start gap-3 p-4 rounded-xl border transition-all duration-200',
        variants[variant]
      )}
    >
      <div className="flex-shrink-0">
        {icons[variant]}
      </div>
      
      <div className="flex-1 min-w-0">
        <h4 className="text-sm font-semibold text-gray-900 dark:text-white mb-1">
          {title}
        </h4>
        {description && (
          <p className="text-sm text-gray-700 dark:text-gray-300 leading-relaxed">
            {description}
          </p>
        )}
        
        {action && (
          <div className="mt-3">
            <Button
              variant="ghost"
              size="sm"
              onClick={action.onClick}
              className="text-xs font-medium"
            >
              {action.label}
            </Button>
          </div>
        )}
      </div>
      
      {dismissible && onDismiss && (
        <Button
          variant="ghost"
          size="sm"
          onClick={onDismiss}
          className="flex-shrink-0 opacity-60 hover:opacity-100"
          aria-label="Dismiss alert"
        >
          <X className="h-4 w-4" />
        </Button>
      )}
    </div>
  );
}
