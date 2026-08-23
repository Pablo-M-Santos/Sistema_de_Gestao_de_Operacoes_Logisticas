CREATE TABLE permissao
(
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(100)  NOT NULL UNIQUE,
    descricao     VARCHAR(255),
    criado_em     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE INDEX idx_permissao_nome
    ON permissao (nome);
