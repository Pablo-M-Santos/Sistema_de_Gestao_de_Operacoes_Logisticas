package br.com.logicore.modules.permissao.repository;

import br.com.logicore.modules.permissao.entity.Permissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissaoRepository extends JpaRepository<Permissao, Long>, JpaSpecificationExecutor<Permissao> {
    Optional<Permissao> findByNome(String nome);
    boolean existsByNome(String nome);
    boolean existsByNomeAndIdNot(String nome, Long id);
}
