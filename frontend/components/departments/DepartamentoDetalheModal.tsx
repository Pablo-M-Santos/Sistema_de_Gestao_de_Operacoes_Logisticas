"use client";

import { CalendarDays } from "lucide-react";

import { Modal } from "@/components/modal";
import type { Departamento } from "@/types/departamento";

type Props = {
  open: boolean;

  departamento: Departamento | null;

  onCloseAction: () => void;
};

export default function DepartamentoDetalheModal({ open, departamento, onCloseAction }: Props) {
  if (!departamento) {
    return null;
  }

  return (
    <Modal open={open} title="Detalhes do Departamento" size="md" onClose={onCloseAction}>
      <div className="space-y-6">
        <div className="flex items-center gap-4">
          <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-emerald-500/10 text-lg font-bold text-emerald-600 ring-1 ring-emerald-500/20">
            {departamento.sigla}
          </div>

          <div>
            <h3 className="text-xl font-bold text-slate-900">{departamento.nome}</h3>

            <p className="mt-1 text-sm text-slate-500">Sigla: {departamento.sigla}</p>
          </div>
        </div>

        <div className="grid grid-cols-2 gap-5 rounded-2xl bg-slate-50 p-5">
          <InfoItem label="Código" value={`#${String(departamento.id).padStart(3, "0")}`} />

          <InfoItem label="Status" value={<StatusBadge ativo={departamento.ativo} />} />

          <InfoItem label="Criado em" value={formatDate(departamento.criadoEm)} />

          <InfoItem label="Atualizado em" value={formatDate(departamento.atualizadoEm)} />
        </div>

        <div>
          <p className="mb-2 text-sm font-semibold text-slate-700">Descrição</p>

          <div className="rounded-2xl border border-slate-200 bg-white p-4 text-sm leading-6 text-slate-600">
            {departamento.descricao || (
              <span className="text-slate-400">Sem descrição cadastrada.</span>
            )}
          </div>
        </div>

        <div className="flex justify-end border-t border-slate-100 pt-5">
          <button
            type="button"
            onClick={onCloseAction}
            className="h-10 rounded-xl bg-slate-900 px-5 text-sm font-semibold text-white transition hover:bg-slate-800"
          >
            Fechar
          </button>
        </div>
      </div>
    </Modal>
  );
}

function InfoItem({ label, value }: { label: string; value: React.ReactNode }) {
  return (
    <div>
      <p className="text-xs font-semibold tracking-wide text-slate-400 uppercase">{label}</p>

      <div className="mt-1 text-sm font-semibold text-slate-800">{value}</div>
    </div>
  );
}

function StatusBadge({ ativo }: { ativo: boolean }) {
  return (
    <span
      className={
        ativo
          ? "inline-flex rounded-full bg-emerald-100 px-3 py-1 text-xs font-semibold text-emerald-700"
          : "inline-flex rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-600"
      }
    >
      {ativo ? "Ativo" : "Inativo"}
    </span>
  );
}

function formatDate(date?: string) {
  if (!date) {
    return "-";
  }

  const [year, month, day] = date.split("-");

  return `${day}/${month}/${year}`;
}
