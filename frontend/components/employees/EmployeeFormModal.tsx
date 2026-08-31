"use client";

import { useEffect, useState } from "react";
import { Modal } from "@/components/modal";

import type { CreateEmployeeRequest, Employee, UpdateEmployeeRequest } from "@/types/employee";

type Props = {
  open: boolean;
  employee?: Employee | null;
  loading?: boolean;
  onCloseAction: () => void;
  onSaveAction: (data: CreateEmployeeRequest | UpdateEmployeeRequest) => Promise<void>;
};

export type EmployeeFormData = {
  matricula: string;
  nome: string;
  cpf: string;
  rg: string;
  dataNascimento: string;
  telefone: string;
  email: string;
  cargoId: string;
  departamentoId: string;
  cep: string;
  logradouro: string;
  numero: string;
  complemento: string;
  bairro: string;
  cidade: string;
  estado: string;
  pais: string;
  dataAdmissao: string;
};

type FormErrors = Partial<Record<keyof EmployeeFormData, string>>;

type ViaCepResponse = {
  cep: string;
  logradouro: string;
  complemento: string;
  bairro: string;
  localidade: string;
  uf: string;
  estado: string;
  erro?: boolean;
};

const initialForm: EmployeeFormData = {
  matricula: "",
  nome: "",
  cpf: "",
  rg: "",
  dataNascimento: "",
  telefone: "",
  email: "",
  cargoId: "",
  departamentoId: "",
  cep: "",
  logradouro: "",
  numero: "",
  complemento: "",
  bairro: "",
  cidade: "",
  estado: "",
  pais: "Brasil",
  dataAdmissao: "",
};

export default function EmployeeFormModal({
  open,
  employee,
  onCloseAction,
  onSaveAction,
  loading = false,
}: Props) {
  const [form, setForm] = useState<EmployeeFormData>(initialForm);
  const [errors, setErrors] = useState<FormErrors>({});
  const [loadingCep, setLoadingCep] = useState(false);
  const [cepMessage, setCepMessage] = useState("");

  const isEditing = !!employee;

  useEffect(() => {
    if (employee) {
      setForm({
        matricula: employee.matricula || "",
        nome: employee.nome || "",
        cpf: employee.cpf || "",
        rg: employee.rg || "",
        dataNascimento: employee.dataNascimento || "",
        telefone: employee.telefone || "",
        email: employee.email || "",
        cargoId: String(employee.cargoId ?? ""),
        departamentoId: String(employee.departamentoId ?? ""),
        cep: employee.enderecoCep || "",
        logradouro: employee.enderecoLogradouro || "",
        numero: employee.enderecoNumero || "",
        complemento: employee.enderecoComplemento || "",
        bairro: employee.enderecoBairro || "",
        cidade: employee.enderecoCidade || "",
        estado: employee.enderecoEstado || "",
        pais: employee.enderecoPais || "Brasil",
        dataAdmissao: employee.dataAdmissao || "",
      });
    } else {
      setForm(initialForm);
    }

    setErrors({});
    setCepMessage("");
  }, [employee, open]);

  function handleChange(field: keyof EmployeeFormData, value: string) {
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

  async function handleCepBlur() {
    const cep = form.cep.replace(/\D/g, "");

    if (cep.length !== 8) {
      return;
    }

    try {
      setLoadingCep(true);
      setCepMessage("");

      const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);

      if (!response.ok) {
        throw new Error("Erro ao consultar o CEP.");
      }

      const data: ViaCepResponse = await response.json();

      if (data.erro) {
        setCepMessage("CEP não encontrado.");
        return;
      }

      setForm((prev) => ({
        ...prev,
        cep: data.cep,
        logradouro: data.logradouro || "",
        complemento: data.complemento || prev.complemento,
        bairro: data.bairro || "",
        cidade: data.localidade || "",
        estado: data.uf || "",
        pais: "Brasil",
      }));
    } catch (error) {
      console.error("Erro ao consultar CEP:", error);
      setCepMessage("Não foi possível consultar o CEP.");
    } finally {
      setLoadingCep(false);
    }
  }

  function validate(): boolean {
    const newErrors: FormErrors = {};

    if (!form.matricula.trim()) {
      newErrors.matricula = "A matrícula é obrigatória.";
    }

    if (!form.nome.trim()) {
      newErrors.nome = "O nome do funcionário é obrigatório.";
    } else if (form.nome.trim().length < 3) {
      newErrors.nome = "O nome deve ter no mínimo 3 caracteres.";
    }

    if (!form.cpf.trim()) {
      newErrors.cpf = "O CPF é obrigatório.";
    }

    if (!form.dataNascimento) {
      newErrors.dataNascimento = "A data de nascimento é obrigatória.";
    }

    if (!form.email.trim()) {
      newErrors.email = "O e-mail é obrigatório.";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
      newErrors.email = "Informe um e-mail válido.";
    }

    if (!form.cargoId) {
      newErrors.cargoId = "O cargo é obrigatório.";
    }

    if (!form.departamentoId) {
      newErrors.departamentoId = "O departamento é obrigatório.";
    }

    if (!form.dataAdmissao) {
      newErrors.dataAdmissao = "A data de admissão é obrigatória.";
    }

    if (!form.cep.trim()) {
      newErrors.cep = "O CEP é obrigatório.";
    }

    if (!form.numero.trim()) {
      newErrors.numero = "O número é obrigatório.";
    }

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;
  }

  async function handleSubmit() {
    if (!validate()) {
      return;
    }

    const payload = {
      matricula: form.matricula.trim(),
      nome: form.nome.trim(),
      cpf: form.cpf.replace(/\D/g, ""),
      rg: form.rg.trim(),
      dataNascimento: form.dataNascimento,
      telefone: form.telefone.replace(/\D/g, ""),
      email: form.email.trim(),
      cargoId: Number(form.cargoId),
      departamentoId: Number(form.departamentoId),
      dataAdmissao: form.dataAdmissao,

      enderecoId: employee?.enderecoId ?? 0,
    };

    try {
      await onSaveAction(payload);
    } catch (error) {
      console.error(error);
    }
  }

  const inputClass = (field: keyof EmployeeFormData) =>
    `h-11 w-full rounded-xl border px-4 text-sm text-slate-800 transition outline-none placeholder:text-slate-400 ${
      errors[field]
        ? "border-rose-400 bg-rose-50/30 focus:border-rose-500 focus:ring-4 focus:ring-rose-500/10"
        : "border-slate-200 bg-slate-50 focus:border-emerald-400 focus:bg-white focus:ring-4 focus:ring-emerald-500/10"
    }`;

  return (
    <Modal
      open={open}
      title={isEditing ? "Editar Funcionário" : "Novo Funcionário"}
      size="xl" // Mantém o xl compatível com o componente
      onClose={onCloseAction}

      // DICA: Se o seu componente Modal aceitar alguma prop como `className`, `modalClassName` ou `maxWidth`,
      // adicione classes como "max-w-7xl" ou "w-[95vw]" diretamente nele se possível.
    >
      {/* Forçamos a largura total com max-w-none para que ele use 100% da largura que o Modal disponibilizar */}
      <div className="max-h-[82vh] w-full max-w-none overflow-y-auto px-2 sm:max-h-[86vh] sm:pr-3">
        {/* Grid de 3 colunas bem distribuídas horizontalmente */}
        <div className="grid grid-cols-1 gap-5 lg:grid-cols-3">
          {/* Coluna 1: Dados Pessoais */}
          <section className="flex flex-col justify-between space-y-4 rounded-2xl border border-slate-100 bg-slate-50/60 p-4 shadow-sm sm:p-5">
            <div>
              <div className="mb-3 border-b border-slate-200/60 pb-2">
                <h3 className="text-sm font-bold text-slate-800">Dados pessoais</h3>
                <p className="mt-0.5 text-xs text-slate-400">Informe os dados básicos.</p>
              </div>

              <div className="space-y-3">
                <Field label="Matrícula" required error={errors.matricula}>
                  <input
                    value={form.matricula}
                    maxLength={30}
                    disabled={isEditing}
                    onChange={(e) => handleChange("matricula", e.target.value)}
                    className={`${inputClass("matricula")} w-full ${
                      isEditing ? "cursor-not-allowed opacity-60" : ""
                    }`}
                    placeholder="EMP001"
                  />
                </Field>

                <Field label="Nome completo" required error={errors.nome}>
                  <input
                    value={form.nome}
                    maxLength={120}
                    onChange={(e) => handleChange("nome", e.target.value)}
                    className={`${inputClass("nome")} w-full`}
                    placeholder="João Silva"
                  />
                </Field>

                <div className="grid grid-cols-2 gap-2">
                  <Field label="CPF" required error={errors.cpf}>
                    <input
                      value={form.cpf}
                      maxLength={14}
                      onChange={(e) => handleChange("cpf", e.target.value)}
                      className={`${inputClass("cpf")} w-full`}
                      placeholder="000.000.000-00"
                    />
                  </Field>

                  <Field label="RG" error={errors.rg}>
                    <input
                      value={form.rg}
                      maxLength={20}
                      onChange={(e) => handleChange("rg", e.target.value)}
                      className={`${inputClass("rg")} w-full`}
                      placeholder="00.000.000-0"
                    />
                  </Field>
                </div>

                <div className="grid grid-cols-2 gap-2">
                  <Field label="Nascimento" required error={errors.dataNascimento}>
                    <input
                      type="date"
                      value={form.dataNascimento}
                      onChange={(e) => handleChange("dataNascimento", e.target.value)}
                      className={`${inputClass("dataNascimento")} w-full`}
                    />
                  </Field>

                  <Field label="Telefone" error={errors.telefone}>
                    <input
                      value={form.telefone}
                      maxLength={20}
                      onChange={(e) => handleChange("telefone", e.target.value)}
                      className={`${inputClass("telefone")} w-full`}
                      placeholder="(11) 99999-9999"
                    />
                  </Field>
                </div>

                <Field label="E-mail" required error={errors.email}>
                  <input
                    type="email"
                    value={form.email}
                    maxLength={120}
                    onChange={(e) => handleChange("email", e.target.value)}
                    className={`${inputClass("email")} w-full`}
                    placeholder="funcionario@empresa.com"
                  />
                </Field>
              </div>
            </div>
          </section>

          {/* Coluna 2: Dados Profissionais */}
          <section className="flex flex-col justify-between space-y-4 rounded-2xl border border-slate-100 bg-slate-50/60 p-4 shadow-sm sm:p-5">
            <div>
              <div className="mb-3 border-b border-slate-200/60 pb-2">
                <h3 className="text-sm font-bold text-slate-800">Dados profissionais</h3>
                <p className="mt-0.5 text-xs text-slate-400">Vincule cargo e depto.</p>
              </div>

              <div className="space-y-3">
                <Field label="Cargo" required error={errors.cargoId}>
                  <input
                    type="number"
                    min={1}
                    value={form.cargoId}
                    onChange={(e) => handleChange("cargoId", e.target.value)}
                    className={`${inputClass("cargoId")} w-full`}
                    placeholder="ID do cargo"
                  />
                </Field>

                <Field label="Departamento" required error={errors.departamentoId}>
                  <input
                    type="number"
                    min={1}
                    value={form.departamentoId}
                    onChange={(e) => handleChange("departamentoId", e.target.value)}
                    className={`${inputClass("departamentoId")} w-full`}
                    placeholder="ID do departamento"
                  />
                </Field>

                <Field label="Data de admissão" required error={errors.dataAdmissao}>
                  <input
                    type="date"
                    value={form.dataAdmissao}
                    onChange={(e) => handleChange("dataAdmissao", e.target.value)}
                    className={`${inputClass("dataAdmissao")} w-full`}
                  />
                </Field>
              </div>
            </div>
          </section>

          {/* Coluna 3: Endereço */}
          <section className="flex flex-col justify-between space-y-4 rounded-2xl border border-slate-100 bg-slate-50/60 p-4 shadow-sm sm:p-5">
            <div>
              <div className="mb-3 border-b border-slate-200/60 pb-2">
                <h3 className="text-sm font-bold text-slate-800">Endereço</h3>
                <p className="mt-0.5 text-xs text-slate-400">Busca automática por CEP.</p>
              </div>

              <div className="space-y-3">
                <Field label="CEP" required error={errors.cep}>
                  <div className="relative">
                    <input
                      value={form.cep}
                      maxLength={9}
                      onChange={(e) => handleChange("cep", e.target.value)}
                      onBlur={handleCepBlur}
                      className={`${inputClass("cep")} w-full`}
                      placeholder="00000-000"
                    />
                    {loadingCep && (
                      <span className="absolute top-1/2 right-3 -translate-y-1/2 text-xs text-slate-400">
                        Buscando...
                      </span>
                    )}
                  </div>
                  {cepMessage && (
                    <p className="mt-1.5 text-xs font-medium text-rose-500">{cepMessage}</p>
                  )}
                </Field>

                <Field label="Logradouro">
                  <input
                    value={form.logradouro}
                    readOnly
                    className={`${inputClass("logradouro")} w-full cursor-not-allowed opacity-70`}
                    placeholder="Preenchido pelo CEP"
                  />
                </Field>

                <div className="grid grid-cols-2 gap-2">
                  <Field label="Número" required error={errors.numero}>
                    <input
                      value={form.numero}
                      maxLength={20}
                      onChange={(e) => handleChange("numero", e.target.value)}
                      className={`${inputClass("numero")} w-full`}
                      placeholder="100"
                    />
                  </Field>

                  <Field label="Complemento">
                    <input
                      value={form.complemento}
                      maxLength={100}
                      onChange={(e) => handleChange("complemento", e.target.value)}
                      className={`${inputClass("complemento")} w-full`}
                      placeholder="Sala 1"
                    />
                  </Field>
                </div>

                <div className="grid grid-cols-2 gap-2">
                  <Field label="Bairro">
                    <input
                      value={form.bairro}
                      readOnly
                      className={`${inputClass("bairro")} w-full cursor-not-allowed opacity-70`}
                      placeholder="Bairro"
                    />
                  </Field>

                  <Field label="Cidade">
                    <input
                      value={form.cidade}
                      readOnly
                      className={`${inputClass("cidade")} w-full cursor-not-allowed opacity-70`}
                      placeholder="Cidade"
                    />
                  </Field>
                </div>

                <div className="grid grid-cols-2 gap-2">
                  <Field label="Estado">
                    <input
                      value={form.estado}
                      readOnly
                      className={`${inputClass("estado")} w-full cursor-not-allowed opacity-70`}
                      placeholder="UF"
                    />
                  </Field>

                  <Field label="País">
                    <input
                      value={form.pais}
                      readOnly
                      className={`${inputClass("pais")} w-full cursor-not-allowed opacity-70`}
                    />
                  </Field>
                </div>
              </div>
            </div>
          </section>
        </div>

        {/* Botões de Ação */}
        <div className="mt-6 flex flex-col-reverse gap-3 border-t border-slate-100 pt-4 sm:flex-row sm:justify-end">
          <button
            type="button"
            onClick={onCloseAction}
            className="h-11 w-full rounded-xl border border-slate-200 px-5 text-sm font-semibold text-slate-600 transition hover:bg-slate-50 sm:w-auto"
          >
            Cancelar
          </button>

          <button
            type="button"
            disabled={loading || loadingCep}
            onClick={handleSubmit}
            className="h-11 w-full rounded-xl bg-emerald-600 px-6 text-sm font-semibold text-white shadow-sm transition hover:bg-emerald-700 hover:shadow-md disabled:cursor-not-allowed disabled:opacity-60 sm:w-auto"
          >
            {loading ? "Salvando..." : isEditing ? "Salvar alterações" : "Criar funcionário"}
          </button>
        </div>
      </div>
    </Modal>
  );
}

function Field({
  label,
  required = false,
  error,
  children,
}: {
  label: string;
  required?: boolean;
  error?: string;
  children: React.ReactNode;
}) {
  return (
    <div>
      <label className="mb-1.5 block text-sm font-semibold text-slate-700">
        {label} {required && <span className="text-rose-500">*</span>}
      </label>

      {children}

      {error && <p className="mt-1.5 text-xs font-medium text-rose-500">{error}</p>}
    </div>
  );
}
