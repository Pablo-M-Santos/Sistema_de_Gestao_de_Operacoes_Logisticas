"use client";

import { useMemo, useState } from "react";
import { Search, MoreVertical, Pencil, Eye, Power, Plus, X, ChevronDown } from "lucide-react";
import type { Departamento } from "@/types/departamento";
import { departamentos } from "@/data/departamento";

type Props = {
  onViewAction: (d: Departamento) => void;
  onNewAction?: () => void;
  onEditAction: (d: Departamento) => void;
  onToggleAction: (d: Departamento) => void;
};

export default function DepartamentoTable({ onViewAction, onEditAction, onToggleAction }: Props) {
  const [query, setQuery] = useState("");
  const [status, setStatus] = useState("all");
  const [menuId, setMenuId] = useState<number | null>(null);

  const filtered = useMemo(() => {
    return departamentos.filter((d) => {
      const search =
        !query ||
        d.nome.toLowerCase().includes(query.toLowerCase()) ||
        d.sigla.toLowerCase().includes(query.toLowerCase());

      const statusMatch = status === "all" || d.status === status;

      return search && statusMatch;
    });
  }, [query, status]);

  const hasFilters = query !== "" || status !== "all";

  return (
    <div className="animate-fade-up overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
      {/* HEADER DA TABELA */}

      <div className="border-b border-slate-100 p-5">
        <div className="flex flex-col gap-3 xl:flex-row">
          {/* BUSCA */}

          <div className="relative flex-1">
            <Search className="absolute top-1/2 left-3 h-4 w-4 -translate-y-1/2 text-slate-400" />

            <input
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Buscar por nome ou sigla..."
              className="h-10 w-full rounded-xl border border-slate-200 bg-slate-50 pr-4 pl-10 text-sm focus:border-emerald-400 focus:ring-4 focus:ring-emerald-500/10 focus:outline-none"
            />
          </div>

          <div className="flex gap-2">
            <Select value={status} onChange={setStatus} />

            {hasFilters && (
              <button
                onClick={() => {
                  setQuery("");
                  setStatus("all");
                }}
                className="flex h-10 items-center gap-2 rounded-xl border border-slate-200 px-3 text-sm text-slate-600 hover:bg-slate-50"
              >
                <X className="h-4 w-4" />
                Limpar
              </button>
            )}
          </div>
        </div>

        <p className="mt-3 text-sm text-slate-500">
          <span className="font-semibold text-slate-900">{filtered.length}</span> departamento(s)
          encontrado(s)
        </p>
      </div>

      {/* TABELA */}

      <div className="overflow-x-auto">
        <table className="w-full min-w-[900px]">
          <thead>
            <tr className="border-b border-slate-100 text-left">
              {[
                "ID",
                "Nome",
                "Sigla",
                "Descrição",
                "Status",
                "Criado em",
                "Atualizado em",
                "Ações",
              ].map((header) => (
                <th
                  key={header}
                  className="px-5 py-3 text-xs font-semibold text-slate-400 uppercase"
                >
                  {header}
                </th>
              ))}
            </tr>
          </thead>

          <tbody>
            {filtered.map((d) => (
              <tr key={d.id} className="border-b border-slate-100 transition hover:bg-slate-50">
                <td className="px-5 py-4 font-mono text-sm text-slate-500">
                  #{String(d.id).padStart(3, "0")}
                </td>

                <td className="px-5 py-4">
                  <div className="flex items-center gap-3">
                    <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-emerald-100 text-xs font-bold text-emerald-700">
                      {d.sigla}
                    </div>

                    <span className="text-sm font-semibold text-slate-800">{d.nome}</span>
                  </div>
                </td>

                <td className="px-5 py-4 text-sm font-medium">{d.sigla}</td>

                <td className="max-w-xs truncate px-5 py-4 text-sm text-slate-500">
                  {d.descricao}
                </td>

                <td className="px-5 py-4">
                  <StatusBadge status={d.status} />
                </td>

                <td className="px-5 py-4 text-sm text-slate-500">{formatDate(d.createdAt)}</td>

                <td className="px-5 py-4 text-sm text-slate-500">{formatDate(d.updatedAt)}</td>

                <td className="px-5 py-4 text-right">
                  <RowActions
                    d={d}
                    open={menuId === d.id}
                    setOpen={() => setMenuId(menuId === d.id ? null : d.id)}
                    onViewAction={onViewAction}
                    onEdit={onEditAction}
                    onToggle={onToggleAction}
                  />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function RowActions({ d, open, setOpen, onViewAction, onEdit, onToggle }: any) {
  return (
    <div className="relative">
      <button
        onClick={setOpen}
        className="flex h-8 w-8 items-center justify-center rounded-lg text-slate-400 hover:bg-slate-100"
      >
        <MoreVertical size={18} />
      </button>

      {open && (
        <div className="absolute top-9 right-0 z-20 w-44 rounded-xl border border-slate-200 bg-white py-2 shadow-xl">
          <MenuItem icon={Eye} label="Visualizar" onClick={() => onViewAction(d)} />

          <MenuItem icon={Pencil} label="Editar" onClick={() => onEdit(d)} />

          <MenuItem
            icon={Power}
            label={d.status === "active" ? "Inativar" : "Ativar"}
            onClick={() => onToggle(d)}
          />
        </div>
      )}
    </div>
  );
}

function MenuItem({ icon: Icon, label, onClick }: any) {
  return (
    <button
      onClick={onClick}
      className="flex w-full items-center gap-2 px-3 py-2 text-sm text-slate-700 hover:bg-slate-50"
    >
      <Icon size={16} />

      {label}
    </button>
  );
}

function Select({ value, onChange }: any) {
  return (
    <div className="relative">
      <select
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="h-10 rounded-xl border border-slate-200 bg-white px-3 pr-8 text-sm"
      >
        <option value="all">Status</option>

        <option value="active">Ativo</option>

        <option value="inactive">Inativo</option>
      </select>

      <ChevronDown className="pointer-events-none absolute top-3 right-2 h-4 w-4 text-slate-400" />
    </div>
  );
}

function StatusBadge({ status }: { status: "active" | "inactive" }) {
  return (
    <span
      className={`rounded-full px-2.5 py-1 text-xs font-semibold ${status === "active" ? "bg-emerald-100 text-emerald-700" : "bg-slate-100 text-slate-600"} `}
    >
      {status === "active" ? "Ativo" : "Inativo"}
    </span>
  );
}

function formatDate(date: string) {
  const [y, m, d] = date.split("-");

  return `${d}/${m}/${y}`;
}
