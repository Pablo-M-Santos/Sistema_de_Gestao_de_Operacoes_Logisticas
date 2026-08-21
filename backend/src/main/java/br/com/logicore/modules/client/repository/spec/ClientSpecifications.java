package br.com.logicore.modules.client.repository.spec;

import br.com.logicore.modules.client.entity.Client;
import org.springframework.data.jpa.domain.Specification;

public final class ClientSpecifications {

    private ClientSpecifications() {}

    public static Specification<Client> withSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.trim().isEmpty()) {
                return null;
            }

            String term = "%" + search.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("razaoSocial")), term),
                    cb.like(cb.lower(root.get("nomeFantasia")), term),
                    cb.like(cb.lower(root.get("cnpj")), term),
                    cb.like(cb.lower(root.get("email")), term),
                    cb.like(cb.lower(root.get("telefone")), term),
                    cb.like(cb.lower(root.get("contatoPrincipal")), term)
            );
        };
    }

    public static Specification<Client> withStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)) {
                return null;
            }
            return cb.equal(root.get("status"), status.trim().toUpperCase());
        };
    }

    public static Specification<Client> withEnderecoId(Long enderecoId) {
        return (root, query, cb) -> {
            if (enderecoId == null) {
                return null;
            }
            return cb.equal(root.get("endereco").get("id"), enderecoId);
        };
    }
}
