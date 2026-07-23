"use client";

import { createContext, useContext, useMemo, useState, type ReactNode } from "react";

interface SidebarContextData {
  collapsed: boolean;
  toggleSidebar: () => void;
  openSidebar: () => void;
  closeSidebar: () => void;
}

const SidebarContext = createContext<SidebarContextData | null>(null);

interface SidebarProviderProps {
  children: ReactNode;
}

export function SidebarProvider({ children }: SidebarProviderProps) {
  const [collapsed, setCollapsed] = useState(false);

  function toggleSidebar() {
    setCollapsed((prev) => !prev);
  }

  function openSidebar() {
    setCollapsed(false);
  }

  function closeSidebar() {
    setCollapsed(true);
  }

  const value = useMemo(
    () => ({
      collapsed,
      toggleSidebar,
      openSidebar,
      closeSidebar,
    }),
    [collapsed]
  );

  return <SidebarContext.Provider value={value}>{children}</SidebarContext.Provider>;
}

export function useSidebar() {
  const context = useContext(SidebarContext);

  if (!context) {
    throw new Error("useSidebar deve ser utilizado dentro de um SidebarProvider.");
  }

  return context;
}
