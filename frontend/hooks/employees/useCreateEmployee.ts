"use client";

import { useState } from "react";

import { createEmployee } from "@/services/employee.service";

import type {
  CreateEmployeeRequest,
  Employee,
} from "@/types/employee";

export function useCreateEmployee() {
  const [loading, setLoading] = useState(false);

  async function create(
    data: CreateEmployeeRequest
  ): Promise<Employee> {
    try {
      setLoading(true);

      return await createEmployee(data);
    } finally {
      setLoading(false);
    }
  }

  return {
    create,
    loading,
  };
}