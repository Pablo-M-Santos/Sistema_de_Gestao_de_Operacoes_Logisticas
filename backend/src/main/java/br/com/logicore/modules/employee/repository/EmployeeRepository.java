package br.com.logicore.modules.employee.repository;

import br.com.logicore.modules.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {


    Optional<Employee> findByCpf(String cpf);


    Optional<Employee> findByMatricula(String matricula);


    boolean existsByCpf(String cpf);


    boolean existsByMatricula(String matricula);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.status = 'ACTIVE'")
    long countActive();

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.status = 'INACTIVE'")
    long countInactive();

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.endereco IS NOT NULL")
    long countWithAddress();

}