"use client";

import { useState } from "react";

import { getHealth } from "@/services/healthService";

import type { HealthResponse } from "@/types/health";

export function useHealth() {
  const [data, setData] = useState<HealthResponse | null>(null);

  const [loading, setLoading] = useState(false);


  async function refresh() {
    try {
      setLoading(true);

      const response = await getHealth();

      setData(response);

      return response;

    } finally {
      setLoading(false);
    }
  }


  return {
    data,
    loading,
    refresh,
  };
}