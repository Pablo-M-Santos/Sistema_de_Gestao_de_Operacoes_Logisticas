"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

import { navMain, navSecondary, type NavItem } from "@/data/navigation";
import { useSidebar } from "./SidebarProvider";


export default function Sidebar() {
  const pathname = usePathname();
  const { collapsed } = useSidebar();

  return (
    <aside
      className={`
        fixed inset-y-0 left-0 z-30 hidden lg:flex flex-col
        bg-slate-900 text-slate-200
        transition-[width] duration-300 ease-in-out
        ${collapsed ? "w-20" : "w-64"}
      `}
    >
      {/* LOGO */}
      <div
        className={`
          flex h-16 items-center
          border-b border-white/5
          transition-all duration-300
          ${collapsed ? "justify-center px-0" : "gap-3 px-6"}
        `}
      >
        <div className="grid h-10 w-10 shrink-0 place-items-center rounded-xl bg-emerald-600 text-white">
          <svg
            viewBox="0 0 24 24"
            className="h-5 w-5"
            fill="none"
            stroke="currentColor"
            strokeWidth="2.2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <path d="M3 7l9-4 9 4-9 4-9-4z" />
            <path d="M3 12l9 4 9-4" />
            <path d="M3 17l9 4 9-4" />
          </svg>
        </div>

        <div
          className={`
            overflow-hidden whitespace-nowrap
            transition-all duration-300 ease-in-out
            ${
              collapsed
                ? "w-0 opacity-0 -translate-x-4"
                : "w-40 opacity-100 translate-x-0"
            }
          `}
        >
          <h1 className="text-lg font-bold text-white">LogiCore</h1>

          <p className="text-xs uppercase tracking-wider text-slate-400">
            Ops Console
          </p>
        </div>
      </div>

      {/* MENU */}
      <nav className="flex-1 overflow-y-auto px-3 py-5">
        <SectionTitle collapsed={collapsed}>Operação</SectionTitle>

        <div className="space-y-1">
          {navMain.map((item) => (
            <NavItem
              key={item.label}
              item={item}
              active={
                item.route
                  ? pathname === item.route ||
                    pathname.startsWith(`${item.route}/`)
                  : false
              }
            />
          ))}
        </div>

        <div className="my-5 h-px bg-white/5" />

        <SectionTitle collapsed={collapsed}>Sistema</SectionTitle>

        <div className="space-y-1">
          {navSecondary.map((item) => (
            <NavItem
              key={item.label}
              item={item}
              active={
                item.route
                  ? pathname === item.route ||
                    pathname.startsWith(`${item.route}/`)
                  : false
              }
            />
          ))}
        </div>
      </nav>

      {/* USUÁRIO */}
      <div
        className={`
          overflow-hidden
          transition-all duration-300 ease-in-out
          ${collapsed ? "h-0 opacity-0" : "h-auto opacity-100"}
        `}
      >
        <div className="m-3 rounded-xl border border-white/5 bg-slate-800 p-4">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-emerald-600 font-semibold text-white">
              JM
            </div>

            <div className="overflow-hidden whitespace-nowrap">
              <p className="text-sm font-semibold text-white">João Martins</p>

              <p className="text-xs text-slate-400">Gerente de Operações</p>
            </div>
          </div>
        </div>
      </div>
    </aside>
  );
}

function SectionTitle({
  children,
  collapsed,
}: {
  children: React.ReactNode;
  collapsed: boolean;
}) {
  return (
    <p
      className={`
        overflow-hidden whitespace-nowrap
        transition-all duration-300
        text-xs font-semibold uppercase tracking-wider text-slate-500
        ${collapsed ? "h-0 opacity-0 mb-0" : "h-5 opacity-100 mb-2 px-3"}
      `}
    >
      {children}
    </p>
  );
}

interface NavItemProps {
  item: NavItem;
  active: boolean;
}

function NavItem({ item, active }: NavItemProps) {
  const { collapsed } = useSidebar();

  const Icon = item.icon;

  return (
    <Link
      href={item.route ?? "#"}
      title={collapsed ? item.label : ""}
      className={`
        group relative flex items-center rounded-lg
        transition-all duration-300 ease-in-out

        ${collapsed ? "h-12 justify-center" : "gap-3 px-3 py-2.5"}

        ${
          active
            ? "bg-emerald-600/20 text-white"
            : "text-slate-300 hover:bg-white/5 hover:text-white"
        }
      `}
    >
      <Icon
        className={`
          h-5 w-5 shrink-0
          transition-colors duration-300

          ${
            active
              ? "text-emerald-400"
              : "text-slate-400 group-hover:text-white"
          }
        `}
      />

      <span
        className={`
          overflow-hidden whitespace-nowrap
          transition-all duration-300

          ${collapsed ? "w-0 opacity-0" : "w-auto opacity-100"}
        `}
      >
        {item.label}
      </span>

      {!collapsed && item.badge && (
        <span className="ml-auto rounded-md bg-emerald-600/20 px-2 py-0.5 text-xs text-emerald-300">
          {item.badge}
        </span>
      )}

      {!collapsed && active && (
        <span className="ml-auto h-5 w-1 rounded-full bg-emerald-500" />
      )}
    </Link>
  );
}
