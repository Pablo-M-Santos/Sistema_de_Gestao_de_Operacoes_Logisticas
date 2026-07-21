import {
  BarChart3,
  Boxes,
  Building2,
  CreditCard,
  LayoutDashboard,
  LifeBuoy,
  Network,
  Route,
  Settings,
  TruckIcon,
  Users,
} from "lucide-react";

export interface NavItem {
  label: string;
  icon: React.ElementType;
  route?: string;
  badge?: string;
}

export const navMain: NavItem[] = [
  {
    label: "Dashboard",
    icon: LayoutDashboard,
    route: "/",
  },
  {
    label: "Entregas",
    icon: Boxes,
    route: "/deliveries",
    badge: "32",
  },
  {
    label: "Motoristas",
    icon: CreditCard,
    route: "/drivers",
  },
  {
    label: "Funcionários",
    icon: Users,
    route: "/employees",
  },
  {
    label: "Departamentos",
    icon: Network,
    route: "/departments",
  },
  {
    label: "Clientes",
    icon: Building2,
    route: "/customers",
  },
  {
    label: "Veículos",
    icon: TruckIcon,
    route: "/vehicles",
  },
  {
    label: "Rotas",
    icon: Route,
  },
  {
    label: "Relatórios",
    icon: BarChart3,
    route: "/reports",
  },
];

export const navSecondary: NavItem[] = [
  {
    label: "Configurações",
    icon: Settings,
    route: "/settings",
  },
  {
    label: "Suporte",
    icon: LifeBuoy,
  },
];

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
