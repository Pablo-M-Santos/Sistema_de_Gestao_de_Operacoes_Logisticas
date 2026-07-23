CREATE TABLE department
(
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(150)                             NOT NULL UNIQUE,
    descricao     VARCHAR(500),
    sigla         VARCHAR(20),
    status        VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    criado_em     TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);