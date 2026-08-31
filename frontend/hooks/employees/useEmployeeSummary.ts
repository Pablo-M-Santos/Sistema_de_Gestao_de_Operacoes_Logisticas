"use client";

import { useCallback, useEffect, useState } from "react";

import type { EmployeeSummary } from "@/types/employee";

import { getEmployeeSummary } from "@/services/employee.service";

export function useEmployeeSummary() {
  const [data, setData] = useState<EmployeeSummary | null>(null);

  const [loading, setLoading] = useState(true);

  const loadSummary = useCallback(async () => {
    try {
      setLoading(true);

      const summary = await getEmployeeSummary();

      setData(summary);
    } catch (error) {
      console.error(
        "Erro ao carregar o resumo dos funcionários:",
        error
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadSummary();
  }, [loadSummary]);

  return {
    data,
    loading,
    refresh: loadSummary,
  };
}