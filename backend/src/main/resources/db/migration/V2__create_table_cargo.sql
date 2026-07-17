CREATE TABLE cargo
(
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(150) NOT NULL UNIQUE,
    descricao     VARCHAR(500),
    codigo        VARCHAR(20) UNIQUE,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);