import type { Departamento } from "@/types/departamento";

export const departamentos: Departamento[] = [
  {
    id: 1,
    nome: "Tecnologia da Informação",
    sigla: "TI",
    descricao: "Responsável pelos sistemas e infraestrutura tecnológica.",
    status: "active",
    createdAt: "2026-07-10",
    updatedAt: "2026-07-20",
  },

  {
    id: 2,
    nome: "Logística",
    sigla: "LOG",
    descricao: "Gestão das operações de transporte e entregas.",
    status: "active",
    createdAt: "2026-07-08",
    updatedAt: "2026-07-18",
  },

  {
    id: 3,
    nome: "Operações",
    sigla: "OPE",
    descricao: "Coordenação das rotas e equipes de campo.",
    status: "active",
    createdAt: "2026-07-05",
    updatedAt: "2026-07-15",
  },

  {
    id: 4,
    nome: "Financeiro",
    sigla: "FIN",
    descricao: "Controle de contas, pagamentos e recebimentos.",
    status: "active",
    createdAt: "2026-06-28",
    updatedAt: "2026-07-12",
  },

  {
    id: 5,
    nome: "Recursos Humanos",
    sigla: "RH",
    descricao: "Gestão de pessoas, admissões e folha de pagamento.",
    status: "active",
    createdAt: "2026-06-20",
    updatedAt: "2026-07-10",
  },

  {
    id: 6,
    nome: "Manutenção",
    sigla: "MAN",
    descricao: "Manutenção preventiva e corretiva da frota.",
    status: "inactive",
    createdAt: "2026-05-30",
    updatedAt: "2026-06-25",
  },
];
