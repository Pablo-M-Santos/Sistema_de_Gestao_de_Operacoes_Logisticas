"use client";

import Toast from "./Toast";
import type { ToastItem } from "./types";

type Props = {
  toasts: ToastItem[];
  onCloseAction: (id: string) => void;
};

export default function ToastContainer({
  toasts,
  onCloseAction,
}: Props) {
  return (
    <div className="fixed right-6 bottom-6 z-[9999] flex flex-col gap-3">
      {toasts.map((toast) => (
        <Toast
          key={toast.id}
          toast={toast}
          onCloseAction={onCloseAction}
        />
      ))}
    </div>
  );
}