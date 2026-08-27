import { PageResponse } from "@/types/departamento";
import { api } from "./api";

import type {
  CreateEmployeeRequest,
  Employee,
  EmployeeSummary,
  UpdateEmployeeRequest,
} from "@/types/employee";

export async function getEmployeeSummary(): Promise<EmployeeSummary> {
  const response = await api.get("/employees/summary");

  return response.data;
}

export async function getEmployees(
  page = 0,
  search = "",
  nome = "",
  cpf = "",
  cargoId?: number,
  departamentoId?: number
): Promise<PageResponse<Employee>> {
  const response = await api.get("/employees", {
    params: {
      page,
      size: 10,

      search: search.trim() || undefined,

      nome: nome.trim() || undefined,

      cpf: cpf.trim() || undefined,

      cargoId: cargoId || undefined,

      departamentoId: departamentoId || undefined,

      sort: "id,asc",
    },
  });

  return response.data;
}

export async function getEmployeeById(id: number): Promise<Employee> {
  const response = await api.get(`/employees/${id}`);

  return response.data;
}

export async function createEmployee(
  data: CreateEmployeeRequest
): Promise<Employee> {
  const response = await api.post("/employees", data);

  return response.data;
}

export async function updateEmployee(
  id: number,
  data: UpdateEmployeeRequest
): Promise<Employee> {
  const response = await api.put(`/employees/${id}`, data);

  return response.data;
}

export async function deleteEmployee(id: number): Promise<void> {
  await api.delete(`/employees/${id}`);
}