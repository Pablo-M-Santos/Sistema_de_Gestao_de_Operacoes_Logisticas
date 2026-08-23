package br.com.logicore.modules.perfil.repository.spec;

import br.com.logicore.modules.perfil.entity.Perfil;
import org.springframework.data.jpa.domain.Specification;

public final class PerfilSpecifications {

    private PerfilSpecifications() {}

    public static Specification<Perfil> withSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.trim().isEmpty()) {
                return null;
            }

            String term = "%" + search.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("nome")), term),
                    cb.like(cb.lower(root.get("descricao")), term)
            );
        };
    }
}
