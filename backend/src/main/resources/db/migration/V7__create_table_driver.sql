CREATE TABLE driver
(
    id             BIGSERIAL PRIMARY KEY,
    funcionario_id BIGINT      NOT NULL UNIQUE,
    cnh            VARCHAR(20) NOT NULL UNIQUE,
    categoria      VARCHAR(5)  NOT NULL,
    validade_cnh   DATE        NOT NULL,
    observacoes    VARCHAR(500),
    criado_em      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_driver_employee
        FOREIGN KEY (funcionario_id)
            REFERENCES employee (id)
);


CREATE INDEX idx_driver_cnh
    ON driver (cnh);


CREATE INDEX idx_driver_categoria
    ON driver (categoria);


CREATE INDEX idx_driver_funcionario_id
    ON driver (funcionario_id);
