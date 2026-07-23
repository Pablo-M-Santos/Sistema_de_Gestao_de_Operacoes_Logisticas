"use client";

import { useState } from "react";
import { updateDepartment } from "@/services/department.service";
import type { UpdateDepartmentRequest, Departamento } from "@/types/departamento";

export function useUpdateDepartment() {
  const [loading, setLoading] = useState(false);

  async function update(id: number, data: UpdateDepartmentRequest): Promise<Departamento> {
    try {
      setLoading(true);
      return await updateDepartment(id, data);
    } finally {
      setLoading(false);
    }
  }

  return {
    update,
    loading,
  };
}
