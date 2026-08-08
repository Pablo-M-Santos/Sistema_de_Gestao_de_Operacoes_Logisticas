package br.com.logicore.modules.address.repository.spec;

import br.com.logicore.modules.address.entity.Address;
import org.springframework.data.jpa.domain.Specification;

public final class AddressSpecifications {

    private AddressSpecifications() {
    }

    public static Specification<Address> withSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.trim().isEmpty()) {
                return null;
            }

            String term = "%" + search.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("cep")), term),
                    cb.like(cb.lower(root.get("logradouro")), term),
                    cb.like(cb.lower(root.get("numero")), term),
                    cb.like(cb.lower(root.get("complemento")), term),
                    cb.like(cb.lower(root.get("bairro")), term),
                    cb.like(cb.lower(root.get("cidade")), term),
                    cb.like(cb.lower(root.get("estado")), term),
                    cb.like(cb.lower(root.get("pais")), term)
            );
        };
    }

    public static Specification<Address> withCep(String cep) {
        return (root, query, cb) -> {
            if (cep == null || cep.trim().isEmpty()) {
                return null;
            }

            return cb.equal(root.get("cep"), cep.trim());
        };
    }

    public static Specification<Address> withCidade(String cidade) {
        return (root, query, cb) -> {
            if (cidade == null || cidade.trim().isEmpty()) {
                return null;
            }

            return cb.equal(cb.lower(root.get("cidade")), cidade.trim().toLowerCase());
        };
    }

    public static Specification<Address> withEstado(String estado) {
        return (root, query, cb) -> {
            if (estado == null || estado.trim().isEmpty()) {
                return null;
            }

            return cb.equal(cb.lower(root.get("estado")), estado.trim().toLowerCase());
        };
    }

    public static Specification<Address> withPais(String pais) {
        return (root, query, cb) -> {
            if (pais == null || pais.trim().isEmpty()) {
                return null;
            }

            return cb.equal(cb.lower(root.get("pais")), pais.trim().toLowerCase());
        };
    }
}

