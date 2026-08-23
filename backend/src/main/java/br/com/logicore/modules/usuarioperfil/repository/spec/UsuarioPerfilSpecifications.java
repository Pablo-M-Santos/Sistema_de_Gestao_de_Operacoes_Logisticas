package br.com.logicore.modules.usuarioperfil.repository.spec;

import br.com.logicore.modules.usuarioperfil.entity.UsuarioPerfil;
import org.springframework.data.jpa.domain.Specification;

public final class UsuarioPerfilSpecifications {

    private UsuarioPerfilSpecifications() {}

    public static Specification<UsuarioPerfil> withSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.trim().isEmpty()) {
                return null;
            }

            String term = "%" + search.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("usuario").get("nome")), term),
                    cb.like(cb.lower(root.get("perfil").get("nome")), term)
            );
        };
    }

    public static Specification<UsuarioPerfil> withUsuarioId(Long usuarioId) {
        return (root, query, cb) -> {
            if (usuarioId == null) {
                return null;
            }
            return cb.equal(root.get("usuario").get("id"), usuarioId);
        };
    }

    public static Specification<UsuarioPerfil> withPerfilId(Long perfilId) {
        return (root, query, cb) -> {
            if (perfilId == null) {
                return null;
            }
            return cb.equal(root.get("perfil").get("id"), perfilId);
        };
    }
}
