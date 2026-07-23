package br.com.logicore.modules.department.repository;

import br.com.logicore.modules.department.entity.Department;
import br.com.logicore.modules.department.enums.DepartmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    long countByStatus(DepartmentStatus status);

    Page<Department> findAllByOrderByIdAsc(Pageable pageable);
}