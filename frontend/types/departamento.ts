export type PageResponse<T> = {
  content: T[];

  page: number;

  size: number;

  totalElements: number;

  totalPages: number;
};

export type DepartmentSummary = {
  total: number;
  active: number;
  inactive: number;
};

export type DepartmentStatus = "ACTIVE" | "INACTIVE";

export type Departamento = {
  id: number;

  nome: string;

  sigla: string;

  descricao: string;

  status: DepartmentStatus;

  criadoEm: string;

  atualizadoEm: string;
};

export type CreateDepartmentRequest = {
  nome: string;
  sigla: string;
  descricao: string;
};

export type UpdateDepartmentRequest = {
  nome: string;
  sigla: string;
  descricao: string;
};