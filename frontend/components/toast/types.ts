export type ToastType = "success" | "error" | "warning" | "info";

export interface ToastItem {
  id: string;
  type: ToastType;
  title: string;
  description?: string;
  duration?: number;
}

export interface ToastOptions {
  title: string;
  description?: string;
  duration?: number;
}

export interface ToastContextData {
  success(options: ToastOptions): void;
  error(options: ToastOptions): void;
  warning(options: ToastOptions): void;
  info(options: ToastOptions): void;
}