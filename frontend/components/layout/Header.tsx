"use client";

import { Menu } from "lucide-react";
import { useSidebar } from "./SidebarProvider";

export default function Header() {
  const { toggleSidebar } = useSidebar();

  return (
    <header className="sticky top-0 z-20 flex h-16 items-center justify-between border-b border-slate-200 bg-white px-6 shadow-sm">
      {/* Lado esquerdo */}
      <div className="flex items-center gap-4">
        <button
          type="button"
          onClick={toggleSidebar}
          className="rounded-lg p-2 transition-colors hover:bg-slate-100"
        >
          <Menu className="h-5 w-5 text-slate-700" />
        </button>

        <div>
          <h1 className="text-lg font-semibold text-slate-900">LogiCore</h1>

          <p className="text-sm text-slate-500">Sistema de Gestão Logística</p>
        </div>
      </div>

      {/* Lado direito */}
      <div className="flex items-center gap-3">
        <div className="flex h-10 w-10 items-center justify-center rounded-full bg-emerald-600 font-semibold text-white">
          JM
        </div>
      </div>
    </header>
  );
}
