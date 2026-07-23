"use client";

import { useEffect, useRef, useState } from "react";
import { MoreVertical } from "lucide-react";
import type { TableAction } from "./types";

type TableActionsProps<T> = {
  row: T;
  actions: TableAction<T>[];
};

export default function TableActions<T>({
  row,
  actions,
}: TableActionsProps<T>) {
  const [open, setOpen] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (
        containerRef.current &&
        !containerRef.current.contains(event.target as Node)
      ) {
        setOpen(false);
      }
    }

    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const visibleActions = actions.filter(
    (action) => !action.hidden || !action.hidden(row)
  );

  return (
    <div ref={containerRef} className="relative inline-block text-left">
      <button
        type="button"
        onClick={() => setOpen((prev) => !prev)}
        className="flex h-9 w-9 items-center justify-center rounded-lg text-slate-500 transition hover:bg-slate-100"
      >
        <MoreVertical size={18} />
      </button>

      {open && (
        <div className="absolute right-0 z-50 mt-2 w-48 overflow-hidden rounded-xl border border-slate-200 bg-white py-2 shadow-xl">
          {visibleActions.map((action) => {
            const Icon = action.icon;
            const disabled = action.disabled?.(row) ?? false;

            return (
              <button
                key={action.label}
                type="button"
                disabled={disabled}
                onClick={() => {
                  action.onClick(row);
                  setOpen(false);
                }}
                className={[
                  "flex w-full items-center gap-3 px-4 py-2 text-sm transition",
                  disabled
                    ? "cursor-not-allowed opacity-50"
                    : action.danger
                      ? "text-red-600 hover:bg-red-50"
                      : "text-slate-700 hover:bg-slate-50",
                ].join(" ")}
              >
                <Icon size={16} />

                {action.label}
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
}