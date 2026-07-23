"use client";

import { createPortal } from "react-dom";
import { useEffect, useRef, useState } from "react";
import { MoreVertical } from "lucide-react";

import type { TableAction } from "./types";

type TableActionsProps<T> = {
  row: T;
  actions: TableAction<T>[];
};

export default function TableActions<T>({ row, actions }: TableActionsProps<T>) {
  const [open, setOpen] = useState(false);

  const [position, setPosition] = useState({
    top: 0,
    left: 0,
  });

  const buttonRef = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      const target = event.target as HTMLElement;

      if (!target.closest("[data-table-action-menu]")) {
        setOpen(false);
      }
    }

    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  function handleOpen() {
    if (!buttonRef.current) return;

    const rect = buttonRef.current.getBoundingClientRect();

    const menuHeight = 160;
    const spaceBottom = window.innerHeight - rect.bottom;

    const openUp = spaceBottom < menuHeight;

    setPosition({
      top: openUp ? rect.top - menuHeight - 8 : rect.bottom + 8,

      left: rect.right - 192,
    });

    setOpen((prev) => !prev);
  }

  const visibleActions = actions.filter((action) => !action.hidden || !action.hidden(row));

  return (
    <>
      <button
        ref={buttonRef}
        type="button"
        onClick={handleOpen}
        className="flex h-9 w-9 items-center justify-center rounded-lg text-slate-500 transition hover:bg-slate-100"
      >
        <MoreVertical size={18} />
      </button>

      {open &&
        createPortal(
          <div
            data-table-action-menu
            style={{
              top: position.top,
              left: position.left,
            }}
            className="fixed z-[9999] w-48 overflow-hidden rounded-xl border border-slate-200 bg-white py-2 shadow-xl"
          >
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
          </div>,
          document.body
        )}
    </>
  );
}
