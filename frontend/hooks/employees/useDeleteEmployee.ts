"use client";

import { useState } from "react";

import { deleteEmployee } from "@/services/employee.service";

export function useDeleteEmployee() {
  const [loading, setLoading] = useState(false);

  async function remove(id: number): Promise<void> {
    try {
      setLoading(true);

      await deleteEmployee(id);
    } finally {
      setLoading(false);
    }
  }

  return {
    remove,
    loading,
  };
}