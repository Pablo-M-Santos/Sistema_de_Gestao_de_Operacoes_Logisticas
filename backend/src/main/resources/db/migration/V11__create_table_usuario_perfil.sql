CREATE TABLE usuario_perfil
(
    id         BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT    NOT NULL,
    perfil_id  BIGINT    NOT NULL,

    CONSTRAINT fk_usuario_perfil_usuario
        FOREIGN KEY (usuario_id)
            REFERENCES usuario (id),

    CONSTRAINT fk_usuario_perfil_perfil
        FOREIGN KEY (perfil_id)
            REFERENCES perfil (id),

    CONSTRAINT uk_usuario_perfil_usuario_perfil
        UNIQUE (usuario_id, perfil_id)
);


CREATE INDEX idx_usuario_perfil_usuario_id
    ON usuario_perfil (usuario_id);


CREATE INDEX idx_usuario_perfil_perfil_id
    ON usuario_perfil (perfil_id);
