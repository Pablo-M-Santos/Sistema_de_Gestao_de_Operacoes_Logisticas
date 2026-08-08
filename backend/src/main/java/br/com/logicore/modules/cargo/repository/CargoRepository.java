package br.com.logicore.modules.cargo.repository;

import br.com.logicore.modules.cargo.entity.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Long>,
        JpaSpecificationExecutor<Cargo> {

    Optional<Cargo> findByNomeIgnoreCase(String nome);

    Optional<Cargo> findByCodigoIgnoreCase(String codigo);

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByCodigoIgnoreCase(String codigo);

    long countByAtivoTrue();

    long countByAtivoFalse();

}