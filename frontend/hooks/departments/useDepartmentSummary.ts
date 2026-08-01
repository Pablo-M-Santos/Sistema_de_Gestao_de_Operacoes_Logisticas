"use client";

import { useEffect, useState, useCallback } from "react";
import type { DepartmentSummary } from "@/types/departamento";
import { getDepartmentSummary } from "@/services/department.service";

export function useDepartmentSummary() {
  const [data, setData] = useState<DepartmentSummary | null>(null);
  const [loading, setLoading] = useState(true);

  const loadSummary = useCallback(async () => {
    try {
      setLoading(true);
      const summary = await getDepartmentSummary();
      setData(summary);
    } catch (error) {
      console.error("Erro ao carregar o resumo dos departamentos:", error);
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