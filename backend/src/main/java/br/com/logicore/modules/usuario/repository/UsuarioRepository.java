package br.com.logicore.modules.usuario.repository;

import br.com.logicore.modules.usuario.entity.Usuario;
import br.com.logicore.modules.usuario.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByFuncionarioId(Long funcionarioId);
    boolean existsByFuncionarioIdAndIdNot(Long funcionarioId, Long id);
    long countByStatus(UserStatus status);
}
