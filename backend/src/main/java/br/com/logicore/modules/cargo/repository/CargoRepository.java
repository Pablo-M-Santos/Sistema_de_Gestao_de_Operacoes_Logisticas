package br.com.logicore.modules.cargo.repository;

import br.com.logicore.modules.cargo.entity.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CargoRepository extends JpaRepository<Cargo, Long> {

    Optional<Cargo> findByNomeIgnoreCase(String nome);

    Optional<Cargo> findByCodigoIgnoreCase(String codigo);

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByCodigoIgnoreCase(String codigo);

}