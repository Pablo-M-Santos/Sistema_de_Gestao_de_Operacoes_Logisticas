package br.com.logicore.modules.usuarioperfil.repository;

import br.com.logicore.modules.usuarioperfil.entity.UsuarioPerfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioPerfilRepository extends JpaRepository<UsuarioPerfil, Long>, JpaSpecificationExecutor<UsuarioPerfil> {

    boolean existsByUsuarioIdAndPerfilId(Long usuarioId, Long perfilId);

    List<UsuarioPerfil> findByUsuarioId(Long usuarioId);

    List<UsuarioPerfil> findByPerfilId(Long perfilId);

    Optional<UsuarioPerfil> findByUsuarioIdAndPerfilId(Long usuarioId, Long perfilId);
}
