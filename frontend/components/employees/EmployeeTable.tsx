"use client";

import { Eye, Pencil, Power } from "lucide-react";

import type { Employee } from "@/types/employee";

import {
  DataTable,
  TableToolbar,
  StatusBadge,
} from "@/components/table";

import type {
  TableColumn,
  TableAction,
  TableFilter,
} from "@/components/table";

type Props = {
  data: Employee[];

  search: string;
  cargoId: string;
  departamentoId: string;

  onSearchChangeAction: (value: string) => void;
  onCargoChangeAction: (value: string) => void;
  onDepartamentoChangeAction: (value: string) => void;

  pagination: {
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
  };

  onPageChangeAction: (page: number) => void;

  onViewAction: (employee: Employee) => void;
  onEditAction: (employee: Employee) => void;
  onToggleAction: (employee: Employee) => void;
};

export default function EmployeeTable({
  data,
  search,
  cargoId,
  departamentoId,
  onSearchChangeAction,
  onCargoChangeAction,
  onDepartamentoChangeAction,
  pagination,
  onPageChangeAction,
  onViewAction,
  onEditAction,
  onToggleAction,
}: Props) {
  const columns: TableColumn<Employee>[] = [
    {
      key: "id",
      title: "ID",
      render: (employee) => (
        <span className="font-mono text-slate-500">
          #{String(employee.id).padStart(3, "0")}
        </span>
      ),
    },

    {
      key: "matricula",
      title: "Matrícula",
      render: (employee) => (
        <span className="font-mono text-sm font-semibold text-slate-700">
          {employee.matricula}
        </span>
      ),
    },

    {
      key: "nome",
      title: "Funcionário",
      render: (employee) => (
        <div>
          <span className="font-semibold text-slate-800">
            {employee.nome}
          </span>

          <p className="mt-0.5 text-xs text-slate-400">
            {employee.email || "-"}
          </p>
        </div>
      ),
    },

    {
      key: "cpf",
      title: "CPF",
      render: (employee) => (
        <span className="text-slate-600">
          {formatCpf(employee.cpf)}
        </span>
      ),
    },

    {
      key: "cargo",
      title: "Cargo",
      render: (employee) => (
        <div>
          <span className="font-medium text-slate-700">
            {employee.cargoNome || "-"}
          </span>

          {employee.cargoCodigo && (
            <span className="ml-2 rounded-md bg-blue-50 px-2 py-1 text-[10px] font-bold text-blue-600">
              {employee.cargoCodigo}
            </span>
          )}
        </div>
      ),
    },

    {
      key: "departamento",
      title: "Departamento",
      render: (employee) => (
        <div>
          <span className="font-medium text-slate-700">
            {employee.departamentoNome || "-"}
          </span>

          {employee.departamentoSigla && (
            <span className="ml-2 rounded-md bg-emerald-50 px-2 py-1 text-[10px] font-bold text-emerald-600">
              {employee.departamentoSigla}
            </span>
          )}
        </div>
      ),
    },

    {
      key: "status",
      title: "Status",
      render: (employee) => (
        <StatusBadge active={employee.status === "ACTIVE"} />
      ),
    },
  ];

  const actions: TableAction<Employee>[] = [
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
      hidden: (employee) => employee.status === "INACTIVE",
    },

    {
      label: "Ativar",
      icon: Power,
      onClick: onToggleAction,
      hidden: (employee) => employee.status === "ACTIVE",
    },
  ];

  /*
   * Os filtros de cargo e departamento serão exibidos aqui.
   *
   * Por enquanto utilizamos os IDs diretamente.
   * Quando tivermos os endpoints de Cargo e Departamento
   * disponíveis no front, podemos substituir esses inputs/valores
   * por selects com os nomes.
   */
  const filters: TableFilter[] = [];

  return (
    <div className="animate-fade-up overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
      <TableToolbar
        search={search}
        searchPlaceholder="Buscar por nome, matrícula ou CPF..."
        onSearchChangeAction={onSearchChangeAction}
        filters={filters}
        hasFilters={search !== "" || cargoId !== "" || departamentoId !== ""}
        onClearFiltersAction={() => {
          onSearchChangeAction("");
          onCargoChangeAction("");
          onDepartamentoChangeAction("");
        }}
        totalItems={pagination.totalElements}
        itemLabel="funcionário(s)"
      />

      <DataTable
        data={data}
        columns={columns}
        actions={actions}
        getRowIdAction={(employee) => employee.id}
        pagination={pagination}
        onPageChangeAction={onPageChangeAction}
      />
    </div>
  );
}

function formatCpf(cpf?: string) {
  if (!cpf) {
    return "-";
  }

  const value = cpf.replace(/\D/g, "");

  if (value.length !== 11) {
    return cpf;
  }

  return value.replace(
    /(\d{3})(\d{3})(\d{3})(\d{2})/,
    "$1.$2.$3-$4"
  );
}