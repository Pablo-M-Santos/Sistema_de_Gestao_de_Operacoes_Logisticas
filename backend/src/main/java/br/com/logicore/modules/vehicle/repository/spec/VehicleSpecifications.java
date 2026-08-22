package br.com.logicore.modules.vehicle.repository.spec;

import br.com.logicore.modules.vehicle.entity.Vehicle;
import org.springframework.data.jpa.domain.Specification;

public final class VehicleSpecifications {

    private VehicleSpecifications() {}

    public static Specification<Vehicle> withSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.trim().isEmpty()) {
                return null;
            }

            String term = "%" + search.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("placa")), term),
                    cb.like(cb.lower(root.get("renavam")), term),
                    cb.like(cb.lower(root.get("modelo")), term),
                    cb.like(cb.lower(root.get("fabricante")), term)
            );
        };
    }

    public static Specification<Vehicle> withStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)) {
                return null;
            }
            return cb.equal(root.get("status"), status.trim().toUpperCase());
        };
    }

    public static Specification<Vehicle> withAnoFabricacao(Integer anoFabricacao) {
        return (root, query, cb) -> {
            if (anoFabricacao == null) {
                return null;
            }
            return cb.equal(root.get("anoFabricacao"), anoFabricacao);
        };
    }

    public static Specification<Vehicle> withAnoModelo(Integer anoModelo) {
        return (root, query, cb) -> {
            if (anoModelo == null) {
                return null;
            }
            return cb.equal(root.get("anoModelo"), anoModelo);
        };
    }
}
