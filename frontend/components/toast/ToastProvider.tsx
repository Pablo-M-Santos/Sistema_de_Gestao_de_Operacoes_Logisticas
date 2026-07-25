"use client";

import {
  createContext,
  useCallback,
  useMemo,
  useState,
  type ReactNode,
} from "react";

import ToastContainer from "./ToastContainer";

import type {
  ToastContextData,
  ToastItem,
  ToastOptions,
  ToastType,
} from "./types";

export const ToastContext = createContext({} as ToastContextData);

type Props = {
  children: ReactNode;
};

export default function ToastProvider({ children }: Props) {
  const [toasts, setToasts] = useState<ToastItem[]>([]);

  const removeToast = useCallback((id: string) => {
    setToasts((current) => current.filter((toast) => toast.id !== id));
  }, []);

  const addToast = useCallback(
    (type: ToastType, options: ToastOptions) => {
      const id = crypto.randomUUID();

      const toast: ToastItem = {
        id,
        type,
        title: options.title,
        description: options.description,
        duration: options.duration ?? 4000,
      };

      setToasts((current) => [...current, toast]);

      setTimeout(() => {
        removeToast(id);
      }, toast.duration);
    },
    [removeToast]
  );

  const value = useMemo(
    () => ({
      success: (options: ToastOptions) => addToast("success", options),

      error: (options: ToastOptions) => addToast("error", options),

      warning: (options: ToastOptions) => addToast("warning", options),

      info: (options: ToastOptions) => addToast("info", options),
    }),
    [addToast]
  );

  return (
    <ToastContext.Provider value={value}>
      {children}

      <ToastContainer
        toasts={toasts}
        onCloseAction={removeToast}
      />
    </ToastContext.Provider>
  );
}