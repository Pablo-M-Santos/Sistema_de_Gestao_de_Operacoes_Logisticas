"use client";

import { useEffect, useState } from "react";
import { Modal } from "@/components/modal";
import type { Departamento } from "@/types/departamento";

type Props = {
  open: boolean;
  departamento?: Departamento | null;
  loading?: boolean;
  onCloseAction: () => void;
  onSaveAction: (data: DepartamentoFormData) => Promise<void>;
};

export type DepartamentoFormData = {
  nome: string;
  sigla: string;
  descricao: string;
};

type FormErrors = {
  nome?: string;
  sigla?: string;
  descricao?: string;
};

export default function DepartamentoFormModal({
  open,
  departamento,
  onCloseAction,
  onSaveAction,
  loading = false,
}: Props) {
  const [form, setForm] = useState<DepartamentoFormData>({
    nome: "",
    sigla: "",
    descricao: "",
  });

  const [errors, setErrors] = useState<FormErrors>({});

  const isEditing = !!departamento;

  useEffect(() => {
    if (departamento) {
      setForm({
        nome: departamento.nome,
        sigla: departamento.sigla,
        descricao: departamento.descricao || "",
      });
    } else {
      setForm({
        nome: "",
        sigla: "",
        descricao: "",
      });
    }
    setErrors({});
  }, [departamento, open]);

  function handleChange(field: keyof DepartamentoFormData, value: string) {
    setForm((prev) => ({
      ...prev,
      [field]: value,
    }));

    if (errors[field]) {
      setErrors((prev) => ({
        ...prev,
        [field]: undefined,
      }));
    }
  }

  function validate(): boolean {
    const newErrors: FormErrors = {};

    if (!form.nome || form.nome.trim().length === 0) {
      newErrors.nome = "O nome do departamento é obrigatório.";
    } else if (form.nome.trim().length < 3) {
      newErrors.nome = "O nome deve ter no mínimo 3 caracteres.";
    }

    if (!form.sigla || form.sigla.trim().length === 0) {
      newErrors.sigla = "A sigla é obrigatória.";
    } else if (form.sigla.trim().length < 2) {
      newErrors.sigla = "Mínimo 2 caracteres.";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  }

  async function handleSubmit() {
    if (!validate()) {
      return;
    }

    await onSaveAction({
      nome: form.nome.trim(),
      sigla: form.sigla.trim().toUpperCase(),
      descricao: form.descricao.trim(),
    });
  }

  return (
    <Modal
      open={open}
      title={isEditing ? "Editar Departamento" : "Novo Departamento"}
      size="lg"
      onClose={onCloseAction}
    >
      <div className="space-y-6">
        <div className="grid grid-cols-1 gap-5 md:grid-cols-3">
          <div className="md:col-span-2">
            <label className="mb-1.5 block text-sm font-semibold text-slate-700">
              Nome do departamento <span className="text-rose-500">*</span>
            </label>

            <p className="mb-2 text-xs text-slate-400">
              Informe o nome completo da área organizacional.
            </p>

            <input
              value={form.nome}
              maxLength={80}
              onChange={(e) => handleChange("nome", e.target.value)}
              className={`h-11 w-full rounded-xl border px-4 text-sm text-slate-800 transition outline-none placeholder:text-slate-400 ${
                errors.nome
                  ? "border-rose-400 bg-rose-50/30 focus:border-rose-500 focus:ring-4 focus:ring-rose-500/10"
                  : "border-slate-200 bg-slate-50 focus:border-emerald-400 focus:bg-white focus:ring-4 focus:ring-emerald-500/10"
              }`}
              placeholder="Ex: Tecnologia da Informação"
            />

            {errors.nome && (
              <p className="mt-1.5 text-xs font-medium text-rose-500">{errors.nome}</p>
            )}
          </div>

          <div>
            <label className="mb-1.5 block text-sm font-semibold text-slate-700">
              Sigla <span className="text-rose-500">*</span>
            </label>

            <p className="mb-2 text-xs text-slate-400">Identificação curta.</p>

            <input
              value={form.sigla}
              maxLength={10}
              onChange={(e) => handleChange("sigla", e.target.value.toUpperCase())}
              className={`h-11 w-full rounded-xl border px-4 text-sm font-semibold tracking-wide text-slate-800 uppercase transition outline-none placeholder:text-slate-400 ${
                errors.sigla
                  ? "border-rose-400 bg-rose-50/30 focus:border-rose-500 focus:ring-4 focus:ring-rose-500/10"
                  : "border-slate-200 bg-slate-50 focus:border-emerald-400 focus:bg-white focus:ring-4 focus:ring-emerald-500/10"
              }`}
              placeholder="TI"
            />

            {errors.sigla && (
              <p className="mt-1.5 text-xs font-medium text-rose-500">{errors.sigla}</p>
            )}
          </div>
        </div>

        <div>
          <div className="mb-1.5 flex items-center justify-between">
            <label className="text-sm font-semibold text-slate-700">Descrição</label>
            <span className="text-xs text-slate-400">{form.descricao.length}/250</span>
          </div>

          <p className="mb-2 text-xs text-slate-400">
            Explique a finalidade e responsabilidades do departamento (opcional).
          </p>

          <textarea
            value={form.descricao}
            maxLength={250}
            onChange={(e) => handleChange("descricao", e.target.value)}
            rows={3}
            className="w-full resize-none rounded-xl border border-slate-200 bg-slate-50 p-4 text-sm leading-6 text-slate-800 transition outline-none placeholder:text-slate-400 focus:border-emerald-400 focus:bg-white focus:ring-4 focus:ring-emerald-500/10"
            placeholder="Ex: Responsável pelos sistemas e infraestrutura tecnológica."
          />
        </div>

        <div className="flex justify-end gap-3 border-t border-slate-100 pt-5">
          <button
            type="button"
            onClick={onCloseAction}
            className="h-11 rounded-xl border border-slate-200 px-5 text-sm font-semibold text-slate-600 transition hover:bg-slate-50"
          >
            Cancelar
          </button>

          <button
            type="button"
            disabled={loading}
            onClick={handleSubmit}
            className="h-11 rounded-xl bg-emerald-600 px-6 text-sm font-semibold text-white shadow-sm transition hover:bg-emerald-700 hover:shadow-md disabled:cursor-not-allowed disabled:opacity-60"
          >
            {loading ? "Salvando..." : isEditing ? "Salvar alterações" : "Criar departamento"}
          </button>
        </div>
      </div>
    </Modal>
  );
}
