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
