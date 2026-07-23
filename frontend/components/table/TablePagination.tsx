"use client";

import { ChevronLeft, ChevronRight } from "lucide-react";

import type { TablePaginationProps } from "./types";

export default function TablePagination({ pagination, onPageChangeAction }: TablePaginationProps) {
  const { page, totalPages, totalElements, size } = pagination;

  const start = page * size + 1;

  const end = Math.min((page + 1) * size, totalElements);

  return (
    <div className="flex justify-end border-t border-slate-100 px-5 py-4">
      <div className="flex items-center gap-4">
        <p className="hidden text-sm text-slate-500 sm:block">
          {start}-{end} de <span className="font-semibold text-slate-700">{totalElements}</span>
        </p>

        <div className="flex items-center gap-2">
          <button
            type="button"
            disabled={page === 0}
            onClick={() => onPageChangeAction(page - 1)}
            className="flex h-9 w-9 items-center justify-center rounded-lg border border-slate-200 text-slate-600 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
          >
            <ChevronLeft className="h-4 w-4" />
          </button>

          <div className="flex h-9 min-w-20 items-center justify-center rounded-lg bg-slate-100 px-3 text-sm font-semibold text-slate-700">
            {page + 1} / {totalPages}
          </div>

          <button
            type="button"
            disabled={page + 1 >= totalPages}
            onClick={() => onPageChangeAction(page + 1)}
            className="flex h-9 w-9 items-center justify-center rounded-lg border border-slate-200 text-slate-600 transition hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
          >
            <ChevronRight className="h-4 w-4" />
          </button>
        </div>
      </div>
    </div>
  );
}
