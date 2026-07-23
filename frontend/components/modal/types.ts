import type { ReactNode } from "react";

export type ModalSize = "sm" | "md" | "lg" | "xl";

export type ModalProps = {
  open: boolean;

  title?: string;

  children: ReactNode;

  size?: ModalSize;

  onClose: () => void;
};