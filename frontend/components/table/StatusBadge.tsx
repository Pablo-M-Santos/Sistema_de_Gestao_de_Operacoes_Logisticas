"use client";

type StatusBadgeProps = {
  active: boolean;
  activeLabel?: string;
  inactiveLabel?: string;
};

export default function StatusBadge({
  active,
  activeLabel = "Ativo",
  inactiveLabel = "Inativo",
}: StatusBadgeProps) {
  return (
    <span
      className={[
        "inline-flex items-center rounded-full px-2.5 py-1 text-xs font-semibold",
        active
          ? "bg-emerald-100 text-emerald-700"
          : "bg-slate-100 text-slate-600",
      ].join(" ")}
    >
      {active ? activeLabel : inactiveLabel}
    </span>
  );
}