export type DepartamentoStatus = "active" | "inactive";

export type Departamento = {
  id: number;
  nome: string;
  sigla: string;
  descricao: string;
  status: DepartamentoStatus;
  createdAt: string;
  updatedAt: string;
};
