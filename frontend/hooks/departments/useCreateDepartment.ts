"use client";

import { useState } from "react";

import { createDepartment } from "@/services/department.service";

import type { CreateDepartmentRequest, Departamento } from "@/types/departamento";

export function useCreateDepartment() {
  const [loading, setLoading] = useState(false);

  async function create(data: CreateDepartmentRequest): Promise<Departamento> {
    try {
      setLoading(true);

      return await createDepartment(data);
    } finally {
      setLoading(false);
    }
  }

  return {
    create,
    loading,
  };
}
