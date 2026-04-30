'use client';

import { ChevronLeft, ChevronRight } from 'lucide-react';
import { Button } from './Button';

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  showFirstLast?: boolean;
}

export function Pagination({ currentPage, totalPages, onPageChange, showFirstLast = true }: PaginationProps) {
  if (totalPages <= 1) return null;

  const pages: (number | string)[] = [];
  
  if (totalPages <= 7) {
    for (let i = 0; i < totalPages; i++) pages.push(i);
  } else {
    pages.push(0);
    if (currentPage > 2) pages.push('...');
    for (let i = Math.max(1, currentPage - 1); i <= Math.min(currentPage + 1, totalPages - 2); i++) {
      pages.push(i);
    }
    if (currentPage < totalPages - 2) pages.push('...');
    pages.push(totalPages - 1);
  }

  return (
    <div className="flex items-center justify-center gap-1">
      {showFirstLast && (
        <Button
          variant="ghost"
          size="sm"
          onClick={() => onPageChange(0)}
          disabled={currentPage === 0}
          className="hidden sm:inline-flex"
        >
          <ChevronLeft className="h-4 w-4" />
        </Button>
      )}
      
      {pages.map((page, idx) =>
        typeof page === 'number' ? (
          <Button
            key={idx}
            variant={page === currentPage ? 'primary' : 'ghost'}
            size="sm"
            onClick={() => onPageChange(page)}
            className="min-w-[36px]"
          >
            {page + 1}
          </Button>
        ) : (
          <span key={idx} className="px-2 py-1 text-gray-500">...</span>
        )
      )}
      
      {showFirstLast && (
        <Button
          variant="ghost"
          size="sm"
          onClick={() => onPageChange(totalPages - 1)}
          disabled={currentPage === totalPages - 1}
          className="hidden sm:inline-flex"
        >
          <ChevronRight className="h-4 w-4" />
        </Button>
      )}
    </div>
  );
}