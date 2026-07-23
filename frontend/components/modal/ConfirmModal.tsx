"use client";

import { AlertTriangle } from "lucide-react";
import type { ReactNode } from "react";
import Modal from "./Modal";

type ConfirmModalProps = {
  open: boolean;
  title?: string;
  description: ReactNode;
  confirmLabel?: string;
  cancelLabel?: string;
  danger?: boolean;
  loading?: boolean;
  onConfirmAction: () => void;
  onCancelAction: () => void;
};

export default function ConfirmModal({
  open,
  title = "Confirmar ação",
  description,
  confirmLabel = "Confirmar",
  cancelLabel = "Cancelar",
  danger = false,
  loading = false,
  onConfirmAction,
  onCancelAction,
}: ConfirmModalProps) {
  return (
    <Modal open={open} title={title} size="sm" onClose={loading ? () => {} : onCancelAction}>
      <div className="space-y-5">
        <div className="flex gap-3">
          <div
            className={`flex h-10 w-10 shrink-0 items-center justify-center rounded-xl ${
              danger ? "bg-rose-100 text-rose-600" : "bg-emerald-100 text-emerald-600"
            } `}
          >
            <AlertTriangle className="h-5 w-5" />
          </div>

          <div className="text-sm leading-6 text-slate-600">{description}</div>
        </div>

        <div className="flex justify-end gap-3">
          <button
            type="button"
            disabled={loading}
            onClick={onCancelAction}
            className="h-10 rounded-xl border border-slate-200 px-4 text-sm font-medium text-slate-600 transition hover:bg-slate-50 disabled:opacity-50"
          >
            {cancelLabel}
          </button>

          <button
            type="button"
            disabled={loading}
            onClick={onConfirmAction}
            className={`h-10 rounded-xl px-4 text-sm font-semibold text-white transition disabled:cursor-not-allowed disabled:opacity-50 ${
              danger ? "bg-rose-600 hover:bg-rose-700" : "bg-emerald-600 hover:bg-emerald-700"
            } `}
          >
            {loading ? "Aguarde..." : confirmLabel}
          </button>
        </div>
      </div>
    </Modal>
  );
}
