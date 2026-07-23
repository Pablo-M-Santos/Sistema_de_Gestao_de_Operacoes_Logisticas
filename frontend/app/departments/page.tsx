"use client";

import { useState } from "react";

import { Network, Plus, Building2, CheckCircle2, XCircle } from "lucide-react";

import AppLayout from "@/components/layout/AppLayout";

import PageHeader from "@/components/header/PageHeader";
import StatCard from "@/components/cards/StatCard";

import DepartamentoTable from "@/components/departments/DepartamentoTable";
import { Departamento } from "@/types/departamento";
import { departamentos } from "@/data/departamento";

import DepartamentoFormModal from "@/components/departments/DepartamentoFormModal";
import DepartamentoDetalheModal from "@/components/departments/DepartamentoDetalheModal";
import DepartamentoConfirmModal from "@/components/departments/DepartamentoConfirmModal";

export default function DepartmentsPage() {
  const [modalOpen, setModalOpen] = useState(false);

  const [editing, setEditing] = useState<Departamento | null>(null);

  const [viewing, setViewing] = useState<Departamento | null>(null);

  const [confirming, setConfirming] = useState<Departamento | null>(null);

  const [confirmAction, setConfirmAction] = useState<"activate" | "deactivate">("deactivate");

  const [saved, setSaved] = useState(false);

  const activeCount = departamentos.filter((d) => d.ativo === true).length;

  const inactiveCount = departamentos.length - activeCount;

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

    setConfirmAction(departamento.ativo ? "deactivate" : "activate");
  }

  function handleConfirmToggle() {
    if (!confirming) {
      return;
    }

    console.log("Alterando status:", confirming);

    setConfirming(null);

    setSaved(true);

    setTimeout(() => {
      setSaved(false);
    }, 3500);
  }

  function handleSave(data: any) {
    console.log("Salvar departamento:", data);

    setModalOpen(false);

    setSaved(true);

    setTimeout(() => {
      setSaved(false);
    }, 3500);
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
          value={departamentos.length}
          accent="blue"
        />

        <StatCard
          icon={CheckCircle2}
          label="Departamentos ativos"
          value={activeCount}
          accent="brand"
        />

        <StatCard
          icon={XCircle}
          label="Departamentos inativos"
          value={inactiveCount}
          accent="rose"
        />
      </div>

      <DepartamentoTable
        onNewAction={openNew}
        onViewAction={openView}
        onEditAction={openEdit}
        onToggleAction={handleToggle}
      />

      <DepartamentoFormModal
        open={modalOpen}
        departamento={editing}
        onCloseAction={() => setModalOpen(false)}
        onSaveAction={handleSave}
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
