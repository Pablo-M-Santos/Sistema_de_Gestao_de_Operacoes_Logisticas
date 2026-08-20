package br.com.logicore.modules.employee.repository.spec;

import br.com.logicore.modules.employee.entity.Employee;
import org.springframework.data.jpa.domain.Specification;

public final class EmployeeSpecifications {

    private EmployeeSpecifications() {
    }

    public static Specification<Employee> withSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.trim().isEmpty()) {
                return null;
            }

            String term = "%" + search.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("nome")), term),
                    cb.like(cb.lower(root.get("cpf")), term),
                    cb.like(cb.lower(root.get("matricula")), term),
                    cb.like(cb.lower(root.get("email")), term),
                    cb.like(cb.lower(root.get("telefone")), term)
            );
        };
    }

    public static Specification<Employee> withNome(String nome) {
        return (root, query, cb) -> {
            if (nome == null || nome.trim().isEmpty()) {
                return null;
            }

            String term = "%" + nome.trim().toLowerCase() + "%";
            return cb.like(cb.lower(root.get("nome")), term);
        };
    }

    public static Specification<Employee> withCpf(String cpf) {
        return (root, query, cb) -> {
            if (cpf == null || cpf.trim().isEmpty()) {
                return null;
            }

            return cb.equal(root.get("cpf"), cpf.trim());
        };
    }

    public static Specification<Employee> withCargoId(Long cargoId) {
        return (root, query, cb) -> {
            if (cargoId == null) {
                return null;
            }

            return cb.equal(root.get("cargo").get("id"), cargoId);
        };
    }

    public static Specification<Employee> withDepartamentoId(Long departamentoId) {
        return (root, query, cb) -> {
            if (departamentoId == null) {
                return null;
            }

            return cb.equal(root.get("departamento").get("id"), departamentoId);
        };
    }
}

