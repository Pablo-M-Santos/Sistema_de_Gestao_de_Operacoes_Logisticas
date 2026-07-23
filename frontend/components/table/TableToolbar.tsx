"use client";

import { Search, X, ChevronDown } from "lucide-react";
import type { TableFilter } from "./types";

type TableToolbarProps = {
  search: string;
  searchPlaceholder?: string;
  onSearchChangeAction: (value: string) => void;

  filters?: TableFilter[];

  onClearFiltersAction?: () => void;

  hasFilters?: boolean;
};

export default function TableToolbar({
  search,
  searchPlaceholder = "Buscar...",
  onSearchChangeAction,
  filters = [],
  onClearFiltersAction,
  hasFilters = false,
}: TableToolbarProps) {
  return (
    <div className="border-b border-slate-100 p-5">
      <div className="flex flex-col gap-3 xl:flex-row">
        <div className="relative flex-1">
          <Search className="absolute top-1/2 left-3 h-4 w-4 -translate-y-1/2 text-slate-400" />

          <input
            value={search}
            onChange={(e) => onSearchChangeAction(e.target.value)}
            placeholder={searchPlaceholder}
            className="h-10 w-full rounded-xl border border-slate-200 bg-slate-50 pr-4 pl-10 text-sm transition outline-none focus:border-emerald-400 focus:ring-4 focus:ring-emerald-500/10"
          />
        </div>

        <div className="flex gap-2">
          {filters.map((filter) => (
            <div key={filter.key} className="relative">
              <select
                value={filter.value}
                onChange={(e) => filter.onChange(e.target.value)}
                className="h-10 rounded-xl border border-slate-200 bg-white px-3 pr-8 text-sm transition outline-none focus:border-emerald-400"
              >
                <option value="all">{filter.label}</option>

                {filter.options.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>

              <ChevronDown className="pointer-events-none absolute top-3 right-2 h-4 w-4 text-slate-400" />
            </div>
          ))}

          {hasFilters && onClearFiltersAction && (
            <button
              type="button"
              onClick={onClearFiltersAction}
              className="flex h-10 items-center gap-2 rounded-xl border border-slate-200 px-3 text-sm text-slate-600 transition hover:bg-slate-50"
            >
              <X className="h-4 w-4" />
              Limpar
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
