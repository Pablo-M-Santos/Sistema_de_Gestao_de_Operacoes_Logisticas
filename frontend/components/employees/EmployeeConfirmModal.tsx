"use client";

import { ConfirmModal } from "@/components/modal";

import type { Employee } from "@/types/employee";

type ActionType = "activate" | "deactivate";

type Props = {
  open: boolean;
  employee: Employee | null;
  action: ActionType;
  loading?: boolean;
  onCloseAction: () => void;
  onConfirmAction: () => void;
};

export default function EmployeeConfirmModal({
  open,
  employee,
  action,
  loading = false,
  onCloseAction,
  onConfirmAction,
}: Props) {
  if (!employee) {
    return null;
  }

  const config = getConfig(action);

  return (
    <ConfirmModal
      open={open}
      title={config.title}
      description={
        <>
          Tem certeza que deseja <strong>{config.text}</strong> o funcionário{" "}
          <strong>{employee.nome}</strong>?
          <p className="mt-2 text-sm text-slate-500">{config.description}</p>
        </>
      }
      confirmLabel={loading ? "Aguarde..." : config.confirmLabel}
      danger={config.danger}
      loading={loading}
      onConfirmAction={onConfirmAction}
      onCancelAction={onCloseAction}
    />
  );
}

function getConfig(action: ActionType) {
  switch (action) {
    case "activate":
      return {
        title: "Ativar funcionário",
        text: "ativar",
        confirmLabel: "Ativar",
        danger: false,
        description: "O funcionário ficará disponível novamente para utilização no sistema.",
      };

    case "deactivate":
      return {
        title: "Inativar funcionário",
        text: "inativar",
        confirmLabel: "Inativar",
        danger: true,
        description:
          "O funcionário será marcado como inativo e não ficará disponível para novas operações.",
      };

    default:
      throw new Error(`Ação inválida: ${action}`);
  }
}
