CREATE TABLE address
(
    id            BIGSERIAL PRIMARY KEY,
    cep           VARCHAR(8)   NOT NULL,
    logradouro    VARCHAR(200) NOT NULL,
    numero        VARCHAR(20)  NOT NULL,
    complemento   VARCHAR(200),
    bairro        VARCHAR(150) NOT NULL,
    cidade        VARCHAR(150) NOT NULL,
    estado        VARCHAR(2)   NOT NULL,
    pais          VARCHAR(100) NOT NULL DEFAULT 'Brasil',
    latitude      DECIMAL(10, 8),
    longitude     DECIMAL(11, 8),
    criado_em     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_address_cep ON address (cep);

CREATE INDEX idx_address_cidade_estado
    ON address (cidade, estado);