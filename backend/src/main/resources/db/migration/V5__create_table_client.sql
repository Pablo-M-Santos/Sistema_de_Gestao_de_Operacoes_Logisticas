CREATE TABLE client
(
    id                  BIGSERIAL PRIMARY KEY,
    razao_social        VARCHAR(150) NOT NULL,
    nome_fantasia       VARCHAR(150),
    cnpj                VARCHAR(14)  NOT NULL UNIQUE,
    inscricao_estadual  VARCHAR(30),
    telefone            VARCHAR(20),
    email               VARCHAR(150),
    contato_principal   VARCHAR(150),
    endereco_id         BIGINT,
    status              VARCHAR(30)  NOT NULL,
    criado_em           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_client_endereco
        FOREIGN KEY (endereco_id)
            REFERENCES address (id)
);


CREATE INDEX idx_client_cnpj
    ON client (cnpj);


CREATE INDEX idx_client_status
    ON client (status);


CREATE INDEX idx_client_endereco
    ON client (endereco_id);
