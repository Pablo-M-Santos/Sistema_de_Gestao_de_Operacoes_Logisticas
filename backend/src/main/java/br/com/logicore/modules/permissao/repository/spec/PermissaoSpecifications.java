package br.com.logicore.modules.permissao.repository.spec;

import br.com.logicore.modules.permissao.entity.Permissao;
import org.springframework.data.jpa.domain.Specification;

public final class PermissaoSpecifications {

    private PermissaoSpecifications() {}

    public static Specification<Permissao> withSearch(String search) {
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
