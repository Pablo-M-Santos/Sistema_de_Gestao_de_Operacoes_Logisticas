CREATE TABLE employee
(
    id              BIGSERIAL PRIMARY KEY,
    matricula       VARCHAR(20)  NOT NULL UNIQUE,
    nome            VARCHAR(150) NOT NULL,
    cpf             VARCHAR(11)  NOT NULL UNIQUE,
    rg              VARCHAR(20),
    data_nascimento DATE,
    telefone        VARCHAR(20),
    email           VARCHAR(150),
    cargo_id        BIGINT       NOT NULL,
    departamento_id BIGINT       NOT NULL,
    endereco_id     BIGINT,
    data_admissao   DATE         NOT NULL,
    status          VARCHAR(30),
    criado_em       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_employee_cargo
        FOREIGN KEY (cargo_id)
            REFERENCES cargo (id),

    CONSTRAINT fk_employee_departamento
        FOREIGN KEY (departamento_id)
            REFERENCES department (id),

    CONSTRAINT fk_employee_endereco
        FOREIGN KEY (endereco_id)
            REFERENCES address (id)

);


CREATE INDEX idx_employee_nome
    ON employee (nome);


CREATE INDEX idx_employee_departamento
    ON employee (departamento_id);


CREATE INDEX idx_employee_cargo
    ON employee (cargo_id);