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
  totalItems?: number;
  itemLabel?: string;
};

export default function TableToolbar({
  search,
  searchPlaceholder = "Buscar...",
  onSearchChangeAction,
  filters = [],
  onClearFiltersAction,
  hasFilters = false,
  totalItems,
  itemLabel,
}: TableToolbarProps) {
  return (
    <div className="border-b border-slate-100 p-5">
      <div className="flex flex-col gap-3 lg:flex-row">
        {/* Pesquisa */}
        <div className="relative flex-1">
          <Search className="absolute top-1/2 left-3 h-4 w-4 -translate-y-1/2 text-slate-400" />

          <input
            value={search}
            onChange={(e) => onSearchChangeAction(e.target.value)}
            placeholder={searchPlaceholder}
            className="h-11 w-full rounded-xl border border-slate-200 bg-white pr-4 pl-10 text-sm text-slate-700 transition outline-none placeholder:text-slate-400 focus:border-emerald-400 focus:ring-4 focus:ring-emerald-500/10"
          />
        </div>

        {/* Filtros */}
        <div className="flex gap-2">
          {filters.map((filter) => (
            <div key={filter.key} className="relative">
              <select
                value={filter.value}
                onChange={(e) => filter.onChange(e.target.value)}
                className="h-11 appearance-none rounded-xl border border-slate-200 bg-white pr-10 pl-4 text-sm font-medium text-slate-600 transition outline-none hover:border-slate-300 focus:border-emerald-400"
              >
                <option value={filter.defaultValue ?? "all"}>{filter.label}</option>

                {filter.options.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>

              <ChevronDown className="pointer-events-none absolute top-3.5 right-3 h-4 w-4 text-slate-400" />
            </div>
          ))}

          {hasFilters && onClearFiltersAction && (
            <button
              type="button"
              onClick={onClearFiltersAction}
              className="flex h-11 items-center gap-2 rounded-xl border border-slate-200 px-4 text-sm font-medium text-slate-600 transition hover:bg-slate-50"
            >
              <X className="h-4 w-4" />
              Limpar
            </button>
          )}
        </div>
      </div>

      {totalItems !== undefined && (
        <p className="mt-4 text-sm text-slate-500">
          <span className="font-semibold text-slate-900">{totalItems}</span>{" "}
          {itemLabel ?? "registro(s)"} encontrado(s)
        </p>
      )}
    </div>
  );
}
