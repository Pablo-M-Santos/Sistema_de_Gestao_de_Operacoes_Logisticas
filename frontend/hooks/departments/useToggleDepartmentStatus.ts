"use client";

import { useState } from "react";
import { activateDepartment, deactivateDepartment } from "@/services/department.service";

export function useToggleDepartmentStatus() {
  const [loading, setLoading] = useState(false);

  async function toggleStatus(id: number, action: "activate" | "deactivate") {
    try {
      setLoading(true);
      
      if (action === "activate") {
        await activateDepartment(id);
      } else {
        await deactivateDepartment(id);
      }
    } finally {
      setLoading(false);
    }
  }

  return {
    toggleStatus,
    loading,
  };
}