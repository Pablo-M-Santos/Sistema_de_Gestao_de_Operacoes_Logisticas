"use client";

import {
  CalendarDays,
  Clock,
  Hash,
  User,
  BriefcaseBusiness,
  Building2,
  Phone,
  Mail,
  MapPin,
  CreditCard,
} from "lucide-react";

import { Modal } from "@/components/modal";

import type { Employee } from "@/types/employee";

type Props = {
  open: boolean;
  employee: Employee | null;
  onCloseAction: () => void;
};

export default function EmployeeDetalheModal({
  open,
  employee,
  onCloseAction,
}: Props) {
  if (!employee) {
    return null;
  }

  const isAtivo = employee.status === "ACTIVE";

  return (
    <Modal
      open={open}
      title="Detalhes do Funcionário"
      size="lg"
      onClose={onCloseAction}
    >
      <div className="space-y-6">
        {/* Cabeçalho */}
        <div className="flex items-start justify-between gap-4">
          <div className="flex items-center gap-4">
            <div className="flex h-16 w-16 shrink-0 items-center justify-center rounded-2xl bg-emerald-500/10 text-xl font-bold text-emerald-600 ring-1 ring-emerald-500/20">
              {getInitials(employee.nome)}
            </div>

            <div>
              <h3 className="text-xl font-bold text-slate-900">
                {employee.nome}
              </h3>

              <p className="mt-0.5 text-xs font-semibold tracking-wider text-slate-400 uppercase">
                Matrícula:{" "}
                <span className="text-slate-700">
                  {employee.matricula}
                </span>
              </p>
            </div>
          </div>

          <StatusBadge active={isAtivo} />
        </div>

        {/* Informações pessoais */}
        <DetailSection title="Informações pessoais">
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <InfoItem
              icon={Hash}
              label="Código ID"
              value={`#${String(employee.id).padStart(3, "0")}`}
            />

            <InfoItem
              icon={CreditCard}
              label="CPF"
              value={formatCpf(employee.cpf)}
            />

            <InfoItem
              icon={CreditCard}
              label="RG"
              value={employee.rg || "-"}
            />

            <InfoItem
              icon={CalendarDays}
              label="Data de nascimento"
              value={formatDateOnly(employee.dataNascimento)}
            />
          </div>
        </DetailSection>

        {/* Contato */}
        <DetailSection title="Contato">
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <InfoItem
              icon={Phone}
              label="Telefone"
              value={employee.telefone || "-"}
            />

            <InfoItem
              icon={Mail}
              label="E-mail"
              value={employee.email || "-"}
            />
          </div>
        </DetailSection>

        {/* Organização */}
        <DetailSection title="Vínculo organizacional">
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <InfoItem
              icon={BriefcaseBusiness}
              label="Cargo"
              value={
                employee.cargoCodigo
                  ? `${employee.cargoNome} (${employee.cargoCodigo})`
                  : employee.cargoNome || "-"
              }
            />

            <InfoItem
              icon={Building2}
              label="Departamento"
              value={
                employee.departamentoSigla
                  ? `${employee.departamentoNome} (${employee.departamentoSigla})`
                  : employee.departamentoNome || "-"
              }
            />

            <InfoItem
              icon={CalendarDays}
              label="Data de admissão"
              value={formatDateOnly(employee.dataAdmissao)}
            />

            <InfoItem
              icon={Building2}
              label="Situação"
              value={isAtivo ? "Ativo no Sistema" : "Inativo no Sistema"}
            />
          </div>
        </DetailSection>

        {/* Endereço */}
        <DetailSection title="Endereço">
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <InfoItem
              icon={MapPin}
              label="CEP"
              value={formatCep(employee.enderecoCep)}
            />

            <InfoItem
              icon={MapPin}
              label="Logradouro"
              value={employee.enderecoLogradouro || "-"}
            />

            <InfoItem
              icon={MapPin}
              label="Número"
              value={employee.enderecoNumero || "-"}
            />

            <InfoItem
              icon={MapPin}
              label="Complemento"
              value={employee.enderecoComplemento || "-"}
            />

            <InfoItem
              icon={MapPin}
              label="Bairro"
              value={employee.enderecoBairro || "-"}
            />

            <InfoItem
              icon={MapPin}
              label="Cidade"
              value={employee.enderecoCidade || "-"}
            />

            <InfoItem
              icon={MapPin}
              label="Estado"
              value={employee.enderecoEstado || "-"}
            />

            <InfoItem
              icon={MapPin}
              label="País"
              value={employee.enderecoPais || "-"}
            />
          </div>
        </DetailSection>

        {/* Auditoria */}
        <DetailSection title="Informações do sistema">
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <InfoItem
              icon={CalendarDays}
              label="Criado em"
              value={formatDate(employee.criadoEm)}
            />

            <InfoItem
              icon={Clock}
              label="Última atualização"
              value={formatDate(employee.atualizadoEm)}
            />
          </div>
        </DetailSection>
      </div>
    </Modal>
  );
}

function DetailSection({
  title,
  children,
}: {
  title: string;
  children: React.ReactNode;
}) {
  return (
    <div>
      <p className="mb-3 text-xs font-semibold tracking-wider text-slate-500 uppercase">
        {title}
      </p>

      <div className="grid grid-cols-1 gap-4 rounded-2xl border border-slate-100 bg-slate-50 p-4">
        {children}
      </div>
    </div>
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

      <div className="min-w-0">
        <p className="text-[11px] font-semibold tracking-wider text-slate-400 uppercase">
          {label}
        </p>

        <div className="mt-0.5 break-words text-sm font-semibold text-slate-800">
          {value}
        </div>
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
      <span
        className={`h-1.5 w-1.5 rounded-full ${
          active ? "bg-emerald-500" : "bg-rose-500"
        }`}
      />

      {active ? "Ativo" : "Inativo"}
    </span>
  );
}

function getInitials(name: string) {
  return name
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((part) => part.charAt(0))
    .join("")
    .toUpperCase();
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

function formatCep(cep?: string) {
  if (!cep) {
    return "-";
  }

  const value = cep.replace(/\D/g, "");

  if (value.length !== 8) {
    return cep;
  }

  return value.replace(/(\d{5})(\d{3})/, "$1-$2");
}

function formatDateOnly(dateString?: string) {
  if (!dateString) {
    return "-";
  }

  try {
    const date = new Date(`${dateString}T00:00:00`);

    if (isNaN(date.getTime())) {
      return dateString;
    }

    return new Intl.DateTimeFormat("pt-BR").format(date);
  } catch {
    return dateString;
  }
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