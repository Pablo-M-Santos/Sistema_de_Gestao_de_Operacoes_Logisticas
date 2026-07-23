"use client";

import { useEffect, useState } from "react";
import { getDepartments } from "@/services/department.service";
import type { Departamento } from "@/types/departamento";

interface Pagination {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export function useDepartments(page = 0, search = "", status = "ALL") {
  const [departments, setDepartments] = useState<Departamento[]>([]);

  const [pagination, setPagination] = useState<Pagination>({
    page: 0,
    size: 20,
    totalElements: 0,
    totalPages: 0,
  });

  const [loading, setLoading] = useState(true);

  async function loadDepartments(currentPage = page) {
    try {
      setLoading(true);

      const response = await getDepartments(currentPage, search, status);

      setDepartments(response.content);

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
    const timer = setTimeout(() => {
      loadDepartments(page);
    }, 400);

    return () => clearTimeout(timer);
  }, [page, search, status]);

  return {
    departments,
    pagination,
    loading,
    refresh: () => loadDepartments(page),
  };
}
