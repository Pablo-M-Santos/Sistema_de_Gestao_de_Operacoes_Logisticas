"use client";

import type { LucideIcon } from "lucide-react";
import type { ReactNode } from "react";

interface Props {
  icon: LucideIcon;
  label: string;
  value: ReactNode;
  hint?: string;
  accent?: "brand" | "blue" | "rose" | "ink";
  index?: number;
}

const accentMap = {
  brand: {
    bg: "bg-emerald-50",
    text: "text-emerald-600",
    border: "border-emerald-100",
  },

  blue: {
    bg: "bg-blue-50",
    text: "text-blue-600",
    border: "border-blue-100",
  },

  rose: {
    bg: "bg-rose-50",
    text: "text-rose-600",
    border: "border-rose-100",
  },

  ink: {
    bg: "bg-slate-50",
    text: "text-slate-700",
    border: "border-slate-200",
  },
};

export default function StatCard({
  icon: Icon,
  label,
  value,
  hint,
  accent = "ink",
  index = 0,
}: Props) {
  const color = accentMap[accent];

  return (
    <div
      className="animate-fade-up flex items-center justify-between rounded-2xl border border-slate-200 bg-white px-5 py-4 shadow-sm transition-all duration-300 hover:-translate-y-1 hover:shadow-lg"
      style={{
        animationDelay: `${index * 70}ms`,
      }}
    >
      <div className="flex items-center gap-4">
        <div
          className={`flex h-11 w-11 items-center justify-center rounded-xl ${color.bg} ${color.text} border ${color.border} `}
        >
          <Icon className="h-5 w-5" />
        </div>

        <div>
          <p className="text-sm font-medium text-slate-500">{label}</p>

          {hint && <p className="text-xs text-slate-400">{hint}</p>}
        </div>
      </div>

      <span className="text-3xl font-bold tracking-tight text-slate-900">{value}</span>
    </div>
  );
}
