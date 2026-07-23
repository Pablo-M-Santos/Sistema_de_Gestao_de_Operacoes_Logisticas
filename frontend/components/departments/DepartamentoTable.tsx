"use client";

import { useEffect, useMemo, useState } from "react";
import { Eye, Pencil, Power } from "lucide-react";

import type { Departamento } from "@/types/departamento";
import { departamentos } from "@/data/departamento";

import { DataTable, TableToolbar, StatusBadge } from "@/components/table";

import type { TableColumn, TableAction, TableFilter } from "@/components/table";

type Props = {
  onViewAction: (d: Departamento) => void;
  onNewAction?: () => void;
  onEditAction: (d: Departamento) => void;
  onToggleAction: (d: Departamento) => void;
};

export default function DepartamentoTable({ onViewAction, onEditAction, onToggleAction }: Props) {
  const [query, setQuery] = useState("");
  const [status, setStatus] = useState("all");
  const [page, setPage] = useState(0);

  const pageSize = 10;
  const filtered = useMemo(() => {
    return departamentos.filter((d) => {
      const search =
        !query ||
        d.nome.toLowerCase().includes(query.toLowerCase()) ||
        d.sigla.toLowerCase().includes(query.toLowerCase());

      const statusMatch =
        status === "all" || (status === "active" && d.ativo) || (status === "inactive" && !d.ativo);

      return search && statusMatch;
    });
  }, [query, status]);

  const pagedData = useMemo(() => {
    const start = page * pageSize;

    return filtered.slice(start, start + pageSize);
  }, [filtered, page]);

  const columns: TableColumn<Departamento>[] = [
    {
      key: "id",
      title: "ID",
      render: (d) => (
        <span className="font-mono text-slate-500">#{String(d.id).padStart(3, "0")}</span>
      ),
    },

    {
      key: "nome",
      title: "Nome",
      render: (d) => (
        <div className="flex items-center gap-3">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-emerald-100 text-xs font-bold text-emerald-700">
            {d.sigla}
          </div>

          <span className="font-semibold text-slate-800">{d.nome}</span>
        </div>
      ),
    },

    {
      key: "sigla",
      title: "Sigla",
      render: (d) => <span className="font-medium">{d.sigla}</span>,
    },
    {
      key: "descricao",
      title: "Descrição",
      cellClassName: "max-w-xl",
      render: (d) => <span className="block max-w-xl text-slate-500">{d.descricao}</span>,
    },
    {
      key: "ativo",
      title: "Status",
      render: (d) => <StatusBadge active={d.ativo} />,
    },
  ];

  const actions: TableAction<Departamento>[] = [
    {
      label: "Visualizar",
      icon: Eye,
      onClick: onViewAction,
    },

    {
      label: "Editar",
      icon: Pencil,
      onClick: onEditAction,
    },

    {
      label: "Inativar",
      icon: Power,
      onClick: onToggleAction,
      hidden: (d) => !d.ativo,
    },

    {
      label: "Ativar",
      icon: Power,
      onClick: onToggleAction,
      hidden: (d) => d.ativo,
    },
  ];

  const filters: TableFilter[] = [
    {
      key: "ativo",
      label: "Status",
      value: status,
      onChange: setStatus,
      options: [
        {
          label: "Ativo",
          value: "active",
        },
        {
          label: "Inativo",
          value: "inactive",
        },
      ],
    },
  ];

  useEffect(() => {
    setPage(0);
  }, [query, status]);

  return (
    <div className="animate-fade-up overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
      <TableToolbar
        search={query}
        searchPlaceholder="Buscar por nome ou sigla..."
        onSearchChangeAction={setQuery}
        filters={filters}
        hasFilters={query !== "" || status !== "all"}
        onClearFiltersAction={() => {
          setQuery("");
          setStatus("all");
        }}
        totalItems={filtered.length}
        itemLabel="departamento(s)"
      />

      <DataTable
        data={pagedData}
        columns={columns}
        actions={actions}
        getRowIdAction={(d) => d.id}
        pagination={{
          page,
          size: pageSize,
          totalElements: filtered.length,
          totalPages: Math.ceil(filtered.length / pageSize),
        }}
        onPageChangeAction={setPage}
      />
    </div>
  );
}
