CREATE TABLE cargo
(
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(100) NOT NULL UNIQUE,
    codigo        VARCHAR(20)  NOT NULL UNIQUE,
    descricao     VARCHAR(255),
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);