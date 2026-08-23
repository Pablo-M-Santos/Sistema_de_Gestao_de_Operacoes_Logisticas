CREATE TABLE usuario
(
    id             BIGSERIAL PRIMARY KEY,
    nome           VARCHAR(150)  NOT NULL,
    email          VARCHAR(150)  NOT NULL UNIQUE,
    senha          VARCHAR(255)  NOT NULL,
    status         VARCHAR(20)   NOT NULL,
    ultimo_acesso  TIMESTAMP,
    funcionario_id BIGINT        NOT NULL UNIQUE,
    criado_em      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_usuario_employee
        FOREIGN KEY (funcionario_id)
            REFERENCES employee (id)
);


CREATE INDEX idx_usuario_email
    ON usuario (email);


CREATE INDEX idx_usuario_status
    ON usuario (status);


CREATE INDEX idx_usuario_funcionario_id
    ON usuario (funcionario_id);
