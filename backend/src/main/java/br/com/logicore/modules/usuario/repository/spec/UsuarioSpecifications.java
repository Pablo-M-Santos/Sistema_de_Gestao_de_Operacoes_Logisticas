package br.com.logicore.modules.usuario.repository.spec;

import br.com.logicore.modules.usuario.entity.Usuario;
import org.springframework.data.jpa.domain.Specification;

public final class UsuarioSpecifications {

    private UsuarioSpecifications() {}

    public static Specification<Usuario> withSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.trim().isEmpty()) {
                return null;
            }

            String term = "%" + search.trim().toLowerCase() + "%";

            return cb.like(cb.lower(root.get("nome")), term);
        };
    }

    public static Specification<Usuario> withEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("email")), "%" + email.trim().toLowerCase() + "%");
        };
    }

    public static Specification<Usuario> withStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)) {
                return null;
            }
            return cb.equal(root.get("status"), status.trim().toUpperCase());
        };
    }

    public static Specification<Usuario> withFuncionarioId(Long funcionarioId) {
        return (root, query, cb) -> {
            if (funcionarioId == null) {
                return null;
            }
            return cb.equal(root.get("funcionario").get("id"), funcionarioId);
        };
    }
}
