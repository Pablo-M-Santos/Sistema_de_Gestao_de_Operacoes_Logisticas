import type { LucideIcon } from "lucide-react";
import type { ReactNode } from "react";

/**
 * Coluna da tabela
 */
export interface TableColumn<T> {
  /**
   * Identificador único da coluna
   */
  key: keyof T | string;

  /**
   * Título exibido no cabeçalho
   */
  title: string;

  /**
   * Classe CSS do cabeçalho
   */
  headerClassName?: string;

  /**
   * Classe CSS das células
   */
  cellClassName?: string;

  /**
   * Largura da coluna
   */
  width?: string | number;

  /**
   * Renderização personalizada
   */
  render?: (row: T) => ReactNode;
}

/**
 * Ação do menu (...)
 */
export interface TableAction<T> {
  label: string;

  icon: LucideIcon;

  onClick: (row: T) => void;

  danger?: boolean;

  hidden?: (row: T) => boolean;

  disabled?: (row: T) => boolean;
}

/**
 * Opção de filtro
 */
export interface FilterOption {
  label: string;

  value: string;
}

/**
 * Configuração de um filtro
 */
export interface TableFilter {
  key: string;

  label: string;

  value: string;

  options: FilterOption[];

  onChange: (value: string) => void;
}