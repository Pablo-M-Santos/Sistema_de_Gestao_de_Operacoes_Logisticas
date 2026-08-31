"use client";

import { useEffect, useState } from "react";

import { getEmployees } from "@/services/employee.service";

import type { Employee } from "@/types/employee";

interface Pagination {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export function useEmployees(
  page = 0,
  search = "",
  nome = "",
  cpf = "",
  cargoId?: number,
  departamentoId?: number
) {
  const [employees, setEmployees] = useState<Employee[]>([]);

  const [pagination, setPagination] = useState<Pagination>({
    page: 0,
    size: 10,
    totalElements: 0,
    totalPages: 0,
  });

  const [loading, setLoading] = useState(true);

  async function loadEmployees(currentPage = page) {
    try {
      setLoading(true);

      const response = await getEmployees(currentPage, search, nome, cpf, cargoId, departamentoId);

      setEmployees(response.content);

      setPagination({
        page: response.page,
        size: response.size,
        totalElements: response.totalElements,
        totalPages: response.totalPages,
      });
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadEmployees(page);
  }, [page]);

  useEffect(() => {
    const timer = setTimeout(() => {
      loadEmployees(0);
    }, 400);

    return () => clearTimeout(timer);
  }, [search, nome, cpf, cargoId, departamentoId]);

  return {
    employees,
    pagination,
    loading,
    refresh: () => loadEmployees(page),
  };
}
