package br.com.logicore.modules.employee.repository;

import br.com.logicore.modules.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface EmployeeRepository extends JpaRepository<Employee, Long> {


    Optional<Employee> findByCpf(String cpf);


    Optional<Employee> findByMatricula(String matricula);


    boolean existsByCpf(String cpf);


    boolean existsByMatricula(String matricula);

}