"use client";

import { useState } from "react";

import { Network, Plus, Building2, CheckCircle2, XCircle } from "lucide-react";

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

  const [refresh, setRefresh] = useState(0);

  const [editing, setEditing] = useState<Departamento | null>(null);

  const [viewing, setViewing] = useState<Departamento | null>(null);

  const [confirming, setConfirming] = useState<Departamento | null>(null);

  const [confirmAction, setConfirmAction] = useState<"activate" | "deactivate">("deactivate");

  const [saved, setSaved] = useState(false);

  const { create, loading: isCreating } = useCreateDepartment();
  const { update, loading: isUpdating } = useUpdateDepartment();

  const { toggleStatus, loading: isToggling } = useToggleDepartmentStatus();

  const isSaving = isCreating || isUpdating;

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

      setSaved(true);
      setTimeout(() => setSaved(false), 3500);
    } catch (error) {
      console.error("Erro ao alterar status do departamento:", error);
    }
  }

  async function handleSave(data: DepartamentoFormData) {
    try {
      if (editing) {
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

      setModalOpen(false);
      setEditing(null);

      await Promise.all([
        refreshDepartments(),
        refreshSummary ? refreshSummary() : Promise.resolve(),
      ]);

      setSaved(true);
      setTimeout(() => {
        setSaved(false);
      }, 3500);
    } catch (error) {
      console.error("Erro ao salvar departamento", error);
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
        onSearchChangeAction={setSearch}
        onStatusChangeAction={setStatus}
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
      {saved && (
        <div className="animate-fade-up fixed right-6 bottom-6 z-50 flex items-center gap-3 rounded-xl bg-slate-900 px-4 py-3 text-white shadow-xl">
          <div className="grid h-6 w-6 place-items-center rounded-full bg-emerald-500 font-bold">
            ✓
          </div>

          <div>
            <p className="text-sm font-semibold">Departamento salvo</p>

            <p className="text-xs text-slate-400">O registro foi atualizado com sucesso.</p>
          </div>
        </div>
      )}
    </AppLayout>
  );
}
