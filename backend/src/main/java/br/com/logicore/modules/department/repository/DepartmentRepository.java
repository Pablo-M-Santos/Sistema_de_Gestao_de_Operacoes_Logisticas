package br.com.logicore.modules.repository;

import br.com.logicore.modules.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByNome(String nome);
    boolean existsByNomeAndIdNot(String nome, Long id);
}