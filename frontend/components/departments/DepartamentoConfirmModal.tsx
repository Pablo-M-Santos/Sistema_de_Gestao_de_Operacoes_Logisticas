"use client";

import { ConfirmModal } from "@/components/modal";

import type { Departamento } from "@/types/departamento";

type ActionType = "activate" | "deactivate";

type Props = {
  open: boolean;

  departamento: Departamento | null;

  action: ActionType;

  onCloseAction: () => void;

  onConfirmAction: () => void;
};

export default function DepartamentoConfirmModal({
  open,
  departamento,
  action,
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
        </>
      }
      confirmLabel={config.confirmLabel}
      danger={action === "deactivate"}
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
      };

    case "deactivate":
      return {
        title: "Inativar departamento",
        text: "inativar",
        confirmLabel: "Inativar",
      };
  }
}
