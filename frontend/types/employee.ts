export type EmployeeStatus = "ACTIVE" | "INACTIVE";

export type Employee = {
  id: number;
  matricula: string;
  nome: string;
  cpf: string;
  rg: string;
  dataNascimento: string;
  telefone: string;
  email: string;
  cargoId: number;
  cargoNome: string;
  cargoCodigo: string;
  departamentoId: number;
  departamentoNome: string;
  departamentoSigla: string;
  enderecoId: number;
  enderecoCep: string;
  enderecoLogradouro: string;
  enderecoNumero: string;
  enderecoComplemento: string;
  enderecoBairro: string;
  enderecoCidade: string;
  enderecoEstado: string;
  enderecoPais: string;
  enderecoLatitude: number;
  enderecoLongitude: number;
  dataAdmissao: string;
  status: EmployeeStatus;
  criadoEm: string;
  atualizadoEm: string;
};

export type CreateEmployeeRequest = {
  matricula: string;
  nome: string;
  cpf: string;
  rg: string;
  dataNascimento: string;
  telefone: string;
  email: string;
  cargoId: number;
  departamentoId: number;
  enderecoId: number;
  dataAdmissao: string;
};

export type UpdateEmployeeRequest = {
  nome?: string;
  cpf?: string;
  rg?: string;
  dataNascimento?: string;
  telefone?: string;
  email?: string;
  cargoId?: number;
  departamentoId?: number;
  enderecoId?: number;
  dataAdmissao?: string;
  status?: EmployeeStatus;
};

export type EmployeeSummary = {
  total: number;
  active: number;
  inactive: number;
  withAddress: number;
  withoutAddress: number;
};

// Exemplo de como deve estar o seu tipo atual:
// export type ModalSize = "sm" | "md" | "lg" | "xl";

// Atualize para incluir o "3xl":
export type ModalSize = "sm" | "md" | "lg" | "xl" | "3xl";
