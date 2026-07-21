"use client";

import { useState } from "react";

import { Network, Plus, Building2, CheckCircle2, XCircle } from "lucide-react";

import AppLayout from "@/components/layout/AppLayout";

import PageHeader from "@/components/ui/PageHeader";
import StatCard from "@/components/ui/StatCard";



import { departamentos, type Departamento } from "@/data/navigation";
import DepartamentoTable from "@/components/departments/DepartamentoTable";

export default function DepartmentsPage() {
  const [modalOpen, setModalOpen] = useState(false);

  const [editing, setEditing] = useState<Departamento | null>(null);

  const [viewing, setViewing] = useState<Departamento | null>(null);

  const [saved, setSaved] = useState(false);

  const activeCount = departamentos.filter((d) => d.status === "active").length;

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
    console.log("Alterar status:", departamento);

    setSaved(true);

    setTimeout(() => {
      setSaved(false);
    }, 3500);
  }

  function handleSave() {
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

      <div
        className="
grid
grid-cols-1
sm:grid-cols-3
gap-4
mb-6
"
      >
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
        onNew={openNew}
        onView={openView}
        onEdit={openEdit}
        onToggle={handleToggle}
      />

      {saved && (
        <div
          className="
fixed
bottom-6
right-6
z-50
flex
items-center
gap-3
px-4
py-3
rounded-xl
bg-slate-900
text-white
shadow-xl
animate-fade-up
"
        >
          <div
            className="
grid
place-items-center
w-6
h-6
rounded-full
bg-emerald-500
font-bold
"
          >
            ✓
          </div>

          <div>
            <p
              className="
text-sm
font-semibold
"
            >
              Departamento salvo
            </p>

            <p
              className="
text-xs
text-slate-400
"
            >
              O registro foi atualizado com sucesso.
            </p>
          </div>
        </div>
      )}
    </AppLayout>
  );
}
