package br.com.logicore.modules.cargo.repository.spec;

import br.com.logicore.modules.cargo.entity.Cargo;
import org.springframework.data.jpa.domain.Specification;

public final class CargoSpecifications {

    private CargoSpecifications() {
    }

    public static Specification<Cargo> withSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.trim().isEmpty()) {
                return null;
            }

            String term = "%" + search.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("nome")), term),
                    cb.like(cb.lower(root.get("codigo")), term)
            );
        };
    }

    public static Specification<Cargo> withStatus(Boolean ativo) {
        return (root, query, cb) ->
                ativo == null ? null : cb.equal(root.get("ativo"), ativo);
    }

}