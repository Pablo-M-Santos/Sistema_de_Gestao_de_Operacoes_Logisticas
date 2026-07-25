"use client";

import { CheckCircle2, XCircle, AlertTriangle, Info, X } from "lucide-react";
import type { ToastItem } from "./types";

type Props = {
  toast: ToastItem;
  onCloseAction: (id: string) => void;
};

const icons = {
  success: CheckCircle2,
  error: XCircle,
  warning: AlertTriangle,
  info: Info,
};

const colors = {
  success: {
    icon: "text-emerald-600",
    bg: "bg-emerald-50",
  },
  error: {
    icon: "text-rose-600",
    bg: "bg-rose-50",
  },
  warning: {
    icon: "text-amber-600",
    bg: "bg-amber-50",
  },
  info: {
    icon: "text-sky-600",
    bg: "bg-sky-50",
  },
};

export default function Toast({ toast, onCloseAction }: Props) {
  const Icon = icons[toast.type];
  const color = colors[toast.type];

  return (
    <div
      className={`flex w-96 items-start gap-3 rounded-xl border border-slate-200 bg-white p-4 shadow-xl animate-fade-up`}
    >
      <div
        className={`grid h-10 w-10 shrink-0 place-items-center rounded-full ${color.bg}`}
      >
        <Icon className={`h-5 w-5 ${color.icon}`} />
      </div>

      <div className="flex-1">
        <p className="text-sm font-semibold text-slate-900">
          {toast.title}
        </p>

        {toast.description && (
          <p className="mt-1 text-sm text-slate-500">
            {toast.description}
          </p>
        )}
      </div>

      <button
        type="button"
        onClick={() => onCloseAction(toast.id)}
        className="rounded-md p-1 text-slate-400 transition hover:bg-slate-100 hover:text-slate-700"
      >
        <X className="h-4 w-4" />
      </button>
    </div>
  );
}