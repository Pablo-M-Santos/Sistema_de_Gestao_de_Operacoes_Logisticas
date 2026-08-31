"use client";

import { useState } from "react";

import { updateEmployee } from "@/services/employee.service";

import type {
  UpdateEmployeeRequest,
  Employee,
} from "@/types/employee";

export function useUpdateEmployee() {
  const [loading, setLoading] = useState(false);

  async function update(
    id: number,
    data: UpdateEmployeeRequest
  ): Promise<Employee> {
    try {
      setLoading(true);

      return await updateEmployee(id, data);
    } finally {
      setLoading(false);
    }
  }

  return {
    update,
    loading,
  };
}