"use client";

import { useState } from "react";

import { Users, UserPlus, UserCheck, UserX, MapPin } from "lucide-react";

import { useToast } from "@/components/toast";
import AppLayout from "@/components/layout/AppLayout";

import PageHeader from "@/components/header/PageHeader";
import StatCard from "@/components/cards/StatCard";

import EmployeeTable from "@/components/employees/EmployeeTable";
import EmployeeFormModal, { EmployeeFormData } from "@/components/employees/EmployeeFormModal";
import EmployeeDetalheModal from "@/components/employees/EmployeeDetalheModal";
import EmployeeConfirmModal from "@/components/employees/EmployeeConfirmModal";

import type { CreateEmployeeRequest, Employee, UpdateEmployeeRequest } from "@/types/employee";

import { useEmployeeSummary } from "@/hooks/employees/useEmployeeSummary";
import { useEmployees } from "@/hooks/employees/useEmployees";
import { useCreateEmployee } from "@/hooks/employees/useCreateEmployee";
import { useUpdateEmployee } from "@/hooks/employees/useUpdateEmployee";

export default function EmployeesPage() {
  const { data: summary, loading: summaryLoading, refresh: refreshSummary } = useEmployeeSummary();

  const [page, setPage] = useState(0);
  const [search, setSearch] = useState("");
  const [cargoId, setCargoId] = useState("");
  const [departamentoId, setDepartamentoId] = useState("");

  const {
    employees,
    pagination,
    refresh: refreshEmployees,
  } = useEmployees(page, search, cargoId, departamentoId);

  const [modalOpen, setModalOpen] = useState(false);

  const [editing, setEditing] = useState<Employee | null>(null);

  const [viewing, setViewing] = useState<Employee | null>(null);

  const [confirming, setConfirming] = useState<Employee | null>(null);

  const [confirmAction, setConfirmAction] = useState<"activate" | "deactivate">("deactivate");

  const { create, loading: isCreating } = useCreateEmployee();

  const { update, loading: isUpdating } = useUpdateEmployee();

  const isSaving = isCreating || isUpdating;

  const toast = useToast();

  function openNew() {
    setEditing(null);
    setModalOpen(true);
  }

  function openEdit(employee: Employee) {
    setEditing(employee);
    setModalOpen(true);
  }

  function openView(employee: Employee) {
    setViewing(employee);
  }

  function handleToggle(employee: Employee) {
    setConfirming(employee);

    setConfirmAction(employee.status === "ACTIVE" ? "deactivate" : "activate");
  }

  async function handleConfirmToggle() {
    if (!confirming) return;

    try {
      setConfirming(null);

      await Promise.all([refreshEmployees(), refreshSummary()]);

      toast.success({
        title: confirmAction === "activate" ? "Funcionário ativado" : "Funcionário inativado",

        description:
          confirmAction === "activate"
            ? "O funcionário foi ativado com sucesso."
            : "O funcionário foi inativado com sucesso.",
      });
    } catch (error) {
      console.error("Erro ao alterar status do funcionário:", error);

      toast.error({
        title: "Erro ao alterar status",
        description: "Não foi possível alterar o status do funcionário.",
      });
    }
  }

  async function handleSave(data: CreateEmployeeRequest | UpdateEmployeeRequest) {
    try {
      const isEditing = !!editing;

      if (isEditing) {
        await update(editing.id, data);
      } else {
        await create(data as CreateEmployeeRequest);
      }

      setModalOpen(false);
      setEditing(null);

      await Promise.all([refreshEmployees(), refreshSummary()]);

      toast.success({
        title: isEditing ? "Funcionário atualizado" : "Funcionário criado",

        description: isEditing
          ? "As alterações foram salvas com sucesso."
          : "O funcionário foi cadastrado com sucesso.",
      });
    } catch (error) {
      console.error("Erro ao salvar funcionário:", error);

      toast.error({
        title: "Erro ao salvar",
        description: "Não foi possível salvar o funcionário.",
      });
    }
  }

  return (
    <AppLayout>
      <PageHeader
        title="Funcionários"
        subtitle="Gerencie os funcionários da organização."
        crumbs={[
          {
            label: "Sistema",
          },
          {
            label: "Funcionários",
          },
        ]}
        icon={Users}
        action={{
          label: "Novo Funcionário",
          icon: UserPlus,
          onClick: openNew,
        }}
      />

      <div className="mb-6 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard
          icon={Users}
          label="Total de funcionários"
          value={summaryLoading ? "..." : (summary?.total ?? 0)}
          accent="blue"
        />

        <StatCard
          icon={UserCheck}
          label="Funcionários ativos"
          value={summaryLoading ? "..." : (summary?.active ?? 0)}
          accent="brand"
        />

        <StatCard
          icon={UserX}
          label="Funcionários inativos"
          value={summaryLoading ? "..." : (summary?.inactive ?? 0)}
          accent="rose"
        />

        <StatCard
          icon={MapPin}
          label="Com endereço"
          value={summaryLoading ? "..." : (summary?.withAddress ?? 0)}
          accent="ink"
        />
      </div>

      <EmployeeTable
        data={employees}
        search={search}
        cargoId={cargoId}
        departamentoId={departamentoId}
        onSearchChangeAction={(value) => {
          setSearch(value);
          setPage(0);
        }}
        onCargoChangeAction={(value) => {
          setCargoId(value);
          setPage(0);
        }}
        onDepartamentoChangeAction={(value) => {
          setDepartamentoId(value);
          setPage(0);
        }}
        pagination={pagination}
        onPageChangeAction={setPage}
        onViewAction={openView}
        onEditAction={openEdit}
        onToggleAction={handleToggle}
      />

      <EmployeeFormModal
        open={modalOpen}
        employee={editing}
        onCloseAction={() => {
          setModalOpen(false);
          setEditing(null);
        }}
        onSaveAction={handleSave}
        loading={isSaving}
      />

      <EmployeeDetalheModal
        open={!!viewing}
        employee={viewing}
        onCloseAction={() => setViewing(null)}
      />

      <EmployeeConfirmModal
        open={!!confirming}
        employee={confirming}
        action={confirmAction}
        loading={isUpdating}
        onCloseAction={() => setConfirming(null)}
        onConfirmAction={handleConfirmToggle}
      />
    </AppLayout>
  );
}
