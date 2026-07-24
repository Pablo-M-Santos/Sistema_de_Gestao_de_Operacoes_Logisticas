package br.com.logicore.modules.department.repository.spec;




import br.com.logicore.modules.department.entity.Department;
import br.com.logicore.modules.department.enums.DepartmentStatus;
import org.springframework.data.jpa.domain.Specification;

public final class DepartmentSpecifications {
    private DepartmentSpecifications() {}

    public static Specification<Department> withSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.trim().isEmpty()) {
                return null;
            }

            String term = "%" + search.trim().toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("nome")), term),
                    cb.like(cb.lower(root.get("sigla")), term)
            );
        };
    }

    public static Specification<Department> withStatus(DepartmentStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }
}