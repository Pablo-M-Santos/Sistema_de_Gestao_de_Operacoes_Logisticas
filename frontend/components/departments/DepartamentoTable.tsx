"use client";

import { useEffect, useMemo, useState } from "react";
import { Eye, Pencil, Power } from "lucide-react";

import type { Departamento } from "@/types/departamento";

import { DataTable, TableToolbar, StatusBadge } from "@/components/table";

import type { TableColumn, TableAction, TableFilter } from "@/components/table";

type Props = {
  data: Departamento[];

  search: string;
  status: string;

  onSearchChangeAction: (value: string) => void;
  onStatusChangeAction: (value: string) => void;

  pagination: {
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
  };

  onPageChangeAction: (page: number) => void;

  onViewAction: (d: Departamento) => void;
  onEditAction: (d: Departamento) => void;
  onToggleAction: (d: Departamento) => void;
};
export default function DepartamentoTable({
  data,
  search,
  status,
  onSearchChangeAction,
  onStatusChangeAction,
  pagination,
  onPageChangeAction,
  onViewAction,
  onEditAction,
  onToggleAction,
}: Props) {
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
      render: (d) => <span className="font-semibold text-slate-800">{d.nome}</span>,
    },

    {
      key: "sigla",
      title: "Sigla",
      render: (d) => (
        <span className="inline-flex h-8 min-w-8 items-center justify-center rounded-lg bg-emerald-100 px-2 text-xs font-bold text-emerald-700">
          {d.sigla}
        </span>
      ),
    },
    {
      key: "descricao",
      title: "Descrição",
      cellClassName: "max-w-xl",
      render: (d) => <span className="block max-w-xl text-slate-500">{d.descricao}</span>,
    },
    {
      key: "status",
      title: "Status",
      render: (d) => <StatusBadge active={d.status === "ACTIVE"} />,
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
      hidden: (d) => d.status !== "INACTIVE",
    },

    {
      label: "Ativar",
      icon: Power,
      onClick: onToggleAction,
      hidden: (d) => d.status !== "ACTIVE",
    },
  ];

  const filters: TableFilter[] = [
    {
      key: "status",
      label: "Todos",
      value: status,
      defaultValue: "ALL",
      onChange: onStatusChangeAction,
      options: [
        {
          label: "Ativos",
          value: "ACTIVE",
        },
        {
          label: "Inativos",
          value: "INACTIVE",
        },
      ],
    },
  ];

  return (
    <div className="animate-fade-up overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
      <TableToolbar
        search={search}
        searchPlaceholder="Buscar por nome ou sigla..."
        onSearchChangeAction={onSearchChangeAction}
        filters={filters}
        hasFilters={search !== "" || status !== "ALL"}
        onClearFiltersAction={() => {
          onSearchChangeAction("");
          onStatusChangeAction("ALL");
        }}
        totalItems={pagination.totalElements}
        itemLabel="departamento(s)"
      />
      <DataTable
        data={data}
        columns={columns}
        actions={actions}
        getRowIdAction={(d) => d.id}
        pagination={pagination}
        onPageChangeAction={onPageChangeAction}
      />
    </div>
  );
}
