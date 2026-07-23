import type { LucideIcon } from "lucide-react";
import type { ReactNode } from "react";

export interface TableColumn<T> {
  key: keyof T | string;

  title: string;

  headerClassName?: string;

  cellClassName?: string;

  width?: string | number;

  render?: (row: T) => ReactNode;
}

export interface TableAction<T> {
  label: string;

  icon: LucideIcon;

  onClick: (row: T) => void;

  danger?: boolean;

  hidden?: (row: T) => boolean;

  disabled?: (row: T) => boolean;
}

export interface FilterOption {
  label: string;

  value: string;
}

export interface TableFilter {
  key: string;

  label: string;

  value: string;

  options: FilterOption[];

  onChange: (value: string) => void;
}
