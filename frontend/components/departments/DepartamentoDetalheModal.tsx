"use client";

import { CalendarDays, Clock, Hash, Building2 } from "lucide-react";
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

  const isAtivo = departamento.status === "ACTIVE";

  return (
    <Modal open={open} title="Detalhes do Departamento" size="md" onClose={onCloseAction}>
      <div className="space-y-6">
        <div className="flex items-start justify-between gap-4">
          <div className="flex items-center gap-4">
            <div className="flex h-16 w-16 shrink-0 items-center justify-center rounded-2xl bg-emerald-500/10 text-xl font-bold text-emerald-600 ring-1 ring-emerald-500/20">
              {departamento.sigla}
            </div>

            <div>
              <h3 className="text-xl font-bold text-slate-900">{departamento.nome}</h3>
              <p className="mt-0.5 text-xs font-semibold tracking-wider text-slate-400 uppercase">
                Sigla: <span className="text-slate-700">{departamento.sigla}</span>
              </p>
            </div>
          </div>

          <StatusBadge active={isAtivo} />
        </div>

        <div className="grid grid-cols-2 gap-4 rounded-2xl border border-slate-100 bg-slate-50 p-4">
          <InfoItem
            icon={Hash}
            label="Código ID"
            value={`#${String(departamento.id).padStart(3, "0")}`}
          />

          <InfoItem
            icon={Building2}
            label="Situação"
            value={isAtivo ? "Ativo no Sistema" : "Inativo no Sistema"}
          />

          <InfoItem
            icon={CalendarDays}
            label="Criado em"
            value={formatDate(departamento.criadoEm)}
          />

          <InfoItem
            icon={Clock}
            label="Última atualização"
            value={formatDate(departamento.atualizadoEm)}
          />
        </div>

        <div>
          <p className="mb-2 text-xs font-semibold tracking-wider text-slate-500 uppercase">
            Descrição do setor
          </p>

          <div className="rounded-2xl border border-slate-200 bg-white p-4 text-sm leading-relaxed text-slate-600">
            {departamento.descricao && departamento.descricao.trim().length > 0 ? (
              departamento.descricao
            ) : (
              <span className="text-slate-400 italic">Nenhuma descrição cadastrada.</span>
            )}
          </div>
        </div>
      </div>
    </Modal>
  );
}

function InfoItem({
  icon: Icon,
  label,
  value,
}: {
  icon: React.ElementType;
  label: string;
  value: React.ReactNode;
}) {
  return (
    <div className="flex items-start gap-3">
      <div className="mt-0.5 rounded-lg border border-slate-200/60 bg-white p-1.5 text-slate-400 shadow-xs">
        <Icon className="h-4 w-4" />
      </div>

      <div>
        <p className="text-[11px] font-semibold tracking-wider text-slate-400 uppercase">{label}</p>
        <div className="mt-0.5 text-sm font-semibold text-slate-800">{value}</div>
      </div>
    </div>
  );
}

function StatusBadge({ active }: { active: boolean }) {
  return (
    <span
      className={`inline-flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-semibold ${
        active
          ? "bg-emerald-100/80 text-emerald-700 ring-1 ring-emerald-600/20"
          : "bg-rose-100/80 text-rose-700 ring-1 ring-rose-600/20"
      }`}
    >
      <span className={`h-1.5 w-1.5 rounded-full ${active ? "bg-emerald-500" : "bg-rose-500"}`} />
      {active ? "Ativo" : "Inativo"}
    </span>
  );
}

function formatDate(dateString?: string) {
  if (!dateString) {
    return "-";
  }

  try {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) {
      return dateString;
    }

    return new Intl.DateTimeFormat("pt-BR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    }).format(date);
  } catch {
    return dateString;
  }
}
