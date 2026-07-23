import type { Departamento } from "@/types/departamento";

export const departamentos: Departamento[] = [
  {
    id: 1,
    nome: "Tecnologia da Informação",
    sigla: "TI",
    descricao: "Responsável pelos sistemas e infraestrutura tecnológica.",
    ativo: true,
    criadoEm: "2026-07-10",
    atualizadoEm: "2026-07-20",
  },

  {
    id: 2,
    nome: "Logística",
    sigla: "LOG",
    descricao: "Gestão das operações de transporte e entregas.",
    ativo: true,
    criadoEm: "2026-07-08",
    atualizadoEm: "2026-07-18",
  },

  {
    id: 3,
    nome: "Operações",
    sigla: "OPE",
    descricao: "Coordenação das rotas e equipes de campo.",
    ativo: true,
    criadoEm: "2026-07-05",
    atualizadoEm: "2026-07-15",
  },

  {
    id: 4,
    nome: "Financeiro",
    sigla: "FIN",
    descricao: "Controle de contas, pagamentos e recebimentos.",
    ativo: true,
    criadoEm: "2026-06-28",
    atualizadoEm: "2026-07-12",
  },

  {
    id: 5,
    nome: "Recursos Humanos",
    sigla: "RH",
    descricao: "Gestão de pessoas, admissões e folha de pagamento.",
    ativo: true,
    criadoEm: "2026-06-20",
    atualizadoEm: "2026-07-10",
  },

  {
    id: 6,
    nome: "Manutenção",
    sigla: "MAN",
    descricao: "Manutenção preventiva e corretiva da frota.",
    ativo: false,
    criadoEm: "2026-05-30",
    atualizadoEm: "2026-06-25",
  },
];
