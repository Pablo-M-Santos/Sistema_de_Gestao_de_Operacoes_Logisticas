"use client";

import { ReactNode } from "react";

import Sidebar from "./Sidebar";
import { useSidebar } from "./SidebarProvider";
import Header from "./Header";


interface AppLayoutProps {
  children: ReactNode;
}

export default function AppLayout({ children }: AppLayoutProps) {
  const { collapsed } = useSidebar();

  return (
    <div className="min-h-screen bg-slate-100">
      <Sidebar />

      <div
        className={`
          transition-all duration-300
          ${collapsed ? "ml-20" : "ml-64"}
        `}
      >
        <Header />

        <main className="p-8">
          {children}
        </main>
      </div>
    </div>
  );
}