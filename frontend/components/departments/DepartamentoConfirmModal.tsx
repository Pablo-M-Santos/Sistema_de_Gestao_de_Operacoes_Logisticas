"use client";

import { ConfirmModal } from "@/components/modal";

import type { Departamento } from "@/types/departamento";

type ActionType = "activate" | "deactivate";

type Props = {
  open: boolean;
  departamento: Departamento | null;
  action: ActionType;
  loading?: boolean;
  onCloseAction: () => void;
  onConfirmAction: () => void;
};

export default function DepartamentoConfirmModal({
  open,
  departamento,
  action,
  loading = false,
  onCloseAction,
  onConfirmAction,
}: Props) {
  if (!departamento) {
    return null;
  }

  const config = getConfig(action);

  return (
    <ConfirmModal
      open={open}
      title={config.title}
      description={
        <>
          Tem certeza que deseja <strong>{config.text}</strong> o departamento{" "}
          <strong>{departamento.nome}</strong>?
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
        title: "Ativar departamento",
        text: "ativar",
        confirmLabel: "Ativar",
        danger: false,
        description: "O departamento ficará disponível novamente para utilização no sistema.",
      };

    case "deactivate":
      return {
        title: "Inativar departamento",
        text: "inativar",
        confirmLabel: "Inativar",
        danger: true,
        description: "O departamento não ficará disponível para novas operações.",
      };
  }
}
