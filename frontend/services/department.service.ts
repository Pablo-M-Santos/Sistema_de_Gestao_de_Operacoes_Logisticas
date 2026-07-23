import { api } from "./api";

import type {
  DepartmentSummary,
  CreateDepartmentRequest,
  Departamento,
  PageResponse,
  UpdateDepartmentRequest,
} from "@/types/departamento";

export async function getDepartmentSummary(): Promise<DepartmentSummary> {
  const response = await api.get("/departments/summary");

  return response.data;
}

export async function getDepartments(
  page = 0,
  search = "",
  status = "ALL"
): Promise<PageResponse<Departamento>> {
  const response = await api.get("/departments", {
    params: {
      page,
      size: 20,
      search,
      status,
    },
  });

  return response.data;
}

export async function createDepartment(data: CreateDepartmentRequest): Promise<Departamento> {
  const response = await api.post("/departments", data);

  return response.data;
}


export async function updateDepartment(
  id: number,
  data: UpdateDepartmentRequest
): Promise<Departamento> {
  const response = await api.put(`/departments/${id}`, data);
  return response.data;
}

export async function activateDepartment(id: number): Promise<void> {
  await api.patch(`/departments/${id}/activate`);
}

export async function deactivateDepartment(id: number): Promise<void> {
  await api.patch(`/departments/${id}/deactivate`);
}