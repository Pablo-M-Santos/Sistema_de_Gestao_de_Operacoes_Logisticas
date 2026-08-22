package br.com.logicore.modules.driver.repository.spec;

import br.com.logicore.modules.driver.entity.Driver;
import org.springframework.data.jpa.domain.Specification;

public final class DriverSpecifications {

    private DriverSpecifications() {}

    public static Specification<Driver> withSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.trim().isEmpty()) {
                return null;
            }

            String term = "%" + search.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("cnh")), term),
                    cb.like(cb.lower(root.get("categoria")), term),
                    cb.like(cb.lower(root.get("observacoes")), term),
                    cb.like(cb.lower(root.get("funcionario").get("nome")), term)
            );
        };
    }

    public static Specification<Driver> withCategoria(String categoria) {
        return (root, query, cb) -> {
            if (categoria == null || categoria.isBlank()) {
                return null;
            }
            return cb.equal(cb.upper(root.get("categoria")), categoria.trim().toUpperCase());
        };
    }

    public static Specification<Driver> withFuncionarioId(Long funcionarioId) {
        return (root, query, cb) -> {
            if (funcionarioId == null) {
                return null;
            }
            return cb.equal(root.get("funcionario").get("id"), funcionarioId);
        };
    }
}
