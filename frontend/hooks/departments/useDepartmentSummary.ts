"use client";

import { useEffect, useState } from "react";

import type { DepartmentSummary } from "@/types/department-summary";

import { getDepartmentSummary } from "@/services/department.service";


export function useDepartmentSummary() {

  const [data, setData] = useState<DepartmentSummary | null>(null);

  const [loading, setLoading] = useState(true);


  useEffect(() => {
    getDepartmentSummary()
      .then(setData)
      .finally(() => setLoading(false));
  }, []);


  return {
    data,
    loading,
  };
}