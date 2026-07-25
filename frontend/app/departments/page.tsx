"use client";

import { useState } from "react";

import { Network, Plus, Building2, CheckCircle2, XCircle } from "lucide-react";
import { useToast } from "@/components/toast";
import AppLayout from "@/components/layout/AppLayout";

import PageHeader from "@/components/header/PageHeader";
import StatCard from "@/components/cards/StatCard";

import DepartamentoTable from "@/components/departments/DepartamentoTable";
import { Departamento } from "@/types/departamento";
import { useDepartmentSummary } from "@/hooks/departments/useDepartmentSummary";
import { useDepartments } from "@/hooks/departments/useDepartments";
import { useUpdateDepartment } from "@/hooks/departments/useUpdateDepartment";
import { useToggleDepartmentStatus } from "@/hooks/departments/useToggleDepartmentStatus";

import DepartamentoFormModal, {
  DepartamentoFormData,
} from "@/components/departments/DepartamentoFormModal";
import DepartamentoDetalheModal from "@/components/departments/DepartamentoDetalheModal";
import DepartamentoConfirmModal from "@/components/departments/DepartamentoConfirmModal";
import { useCreateDepartment } from "@/hooks/departments/useCreateDepartment";

export default function DepartmentsPage() {
  const {
    data: summary,
    loading: summaryLoading,
    refresh: refreshSummary,
  } = useDepartmentSummary();

  const [page, setPage] = useState(0);

  const [search, setSearch] = useState("");

  const [status, setStatus] = useState("ALL");

  const {
    departments,
    pagination,
    refresh: refreshDepartments,
  } = useDepartments(page, search, status);

  const [modalOpen, setModalOpen] = useState(false);

  const [editing, setEditing] = useState<Departamento | null>(null);

  const [viewing, setViewing] = useState<Departamento | null>(null);

  const [confirming, setConfirming] = useState<Departamento | null>(null);

  const [confirmAction, setConfirmAction] = useState<"activate" | "deactivate">("deactivate");

  const { create, loading: isCreating } = useCreateDepartment();
  const { update, loading: isUpdating } = useUpdateDepartment();

  const { toggleStatus, loading: isToggling } = useToggleDepartmentStatus();

  const isSaving = isCreating || isUpdating;

  const toast = useToast();

  function openNew() {
    setEditing(null);

    setModalOpen(true);
  }

  function openEdit(departamento: Departamento) {
    setEditing(departamento);

    setModalOpen(true);
  }

  function openView(departamento: Departamento) {
    setViewing(departamento);
  }

  function handleToggle(departamento: Departamento) {
    setConfirming(departamento);

    setConfirmAction(departamento.status === "ACTIVE" ? "deactivate" : "activate");
  }

  async function handleConfirmToggle() {
    if (!confirming) return;

    try {
      await toggleStatus(confirming.id, confirmAction);

      setConfirming(null);

      await Promise.all([
        refreshDepartments(),
        refreshSummary ? refreshSummary() : Promise.resolve(),
      ]);

      toast.success({
        title: confirmAction === "activate" ? "Departamento ativado" : "Departamento inativado",
        description:
          confirmAction === "activate"
            ? "O departamento foi ativado com sucesso."
            : "O departamento foi inativado com sucesso.",
      });
    } catch (error) {
      console.error("Erro ao alterar status do departamento:", error);

      toast.error({
        title: "Erro ao alterar status",
        description: "Não foi possível alterar o status do departamento.",
      });
    }
  }

  async function handleSave(data: DepartamentoFormData) {
    try {
      const isEditing = !!editing;

      if (isEditing) {
        await update(editing.id, {
          nome: data.nome,
          sigla: data.sigla,
          descricao: data.descricao,
        });
      } else {
        await create({
          nome: data.nome,
          sigla: data.sigla,
          descricao: data.descricao,
        });
      }

      // fecha o modal imediatamente
      setModalOpen(false);
      setEditing(null);

      await Promise.all([
        refreshDepartments(),
        refreshSummary ? refreshSummary() : Promise.resolve(),
      ]);

      toast.success({
        title: isEditing ? "Departamento atualizado" : "Departamento criado",
        description: isEditing
          ? "As alterações foram salvas com sucesso."
          : "O departamento foi cadastrado com sucesso.",
      });
    } catch (error) {
      console.error("Erro ao salvar departamento", error);

      toast.error({
        title: "Erro ao salvar",
        description: "Não foi possível salvar o departamento.",
      });
    }
  }
  return (
    <AppLayout>
      <PageHeader
        title="Departamentos"
        subtitle="Gerencie as áreas e setores da organização."
        crumbs={[
          {
            label: "Sistema",
          },
          {
            label: "Departamentos",
          },
        ]}
        icon={Network}
        action={{
          label: "Novo Departamento",
          icon: Plus,
          onClick: openNew,
        }}
      />

      <div className="mb-6 grid grid-cols-1 gap-4 sm:grid-cols-3">
        <StatCard
          icon={Building2}
          label="Total de departamentos"
          value={summaryLoading ? "..." : (summary?.total ?? 0)}
          accent="blue"
        />

        <StatCard
          icon={CheckCircle2}
          label="Departamentos ativos"
          value={summaryLoading ? "..." : (summary?.active ?? 0)}
          accent="brand"
        />

        <StatCard
          icon={XCircle}
          label="Departamentos inativos"
          value={summaryLoading ? "..." : (summary?.inactive ?? 0)}
          accent="rose"
        />
      </div>

      <DepartamentoTable
        data={departments}
        search={search}
        status={status}
        onSearchChangeAction={(val) => {
          setSearch(val);
          setPage(0);
        }}
        onStatusChangeAction={(val) => {
          setStatus(val);
          setPage(0);
        }}
        pagination={pagination}
        onPageChangeAction={setPage}
        onViewAction={openView}
        onEditAction={openEdit}
        onToggleAction={handleToggle}
      />

      <DepartamentoFormModal
        open={modalOpen}
        departamento={editing}
        onCloseAction={() => {
          setModalOpen(false);
          setEditing(null);
        }}
        onSaveAction={handleSave}
        loading={isSaving}
      />

      <DepartamentoDetalheModal
        open={!!viewing}
        departamento={viewing}
        onCloseAction={() => setViewing(null)}
      />

      <DepartamentoConfirmModal
        open={!!confirming}
        departamento={confirming}
        action={confirmAction}
        loading={isToggling}
        onCloseAction={() => setConfirming(null)}
        onConfirmAction={handleConfirmToggle}
      />
    </AppLayout>
  );
}
