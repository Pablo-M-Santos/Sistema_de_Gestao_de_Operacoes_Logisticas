package br.com.logicore.modules.driver.repository;

import br.com.logicore.modules.driver.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long>, JpaSpecificationExecutor<Driver> {
    boolean existsByCnh(String cnh);
    boolean existsByCnhAndIdNot(String cnh, Long id);
    boolean existsByFuncionarioId(Long funcionarioId);
    boolean existsByFuncionarioIdAndIdNot(Long funcionarioId, Long id);
}
