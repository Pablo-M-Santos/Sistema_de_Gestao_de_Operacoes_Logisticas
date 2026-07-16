CREATE TABLE area_departamento
(
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(150)                             NOT NULL UNIQUE,
    descricao     VARCHAR(500),
    sigla         VARCHAR(20),
    ativo         BOOLEAN                     DEFAULT TRUE NOT NULL,
    criado_em     TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


CREATE INDEX idx_area_dept_nome ON area_departamento (nome);