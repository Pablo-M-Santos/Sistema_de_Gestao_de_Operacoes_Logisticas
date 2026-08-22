CREATE TABLE vehicle
(
    id                BIGSERIAL PRIMARY KEY,
    placa             VARCHAR(10)   NOT NULL UNIQUE,
    renavam           VARCHAR(11)   NOT NULL UNIQUE,
    modelo            VARCHAR(100)  NOT NULL,
    fabricante        VARCHAR(100)  NOT NULL,
    ano_fabricacao    INTEGER       NOT NULL,
    ano_modelo        INTEGER       NOT NULL,
    capacidade_peso   NUMERIC(10,2) NOT NULL,
    capacidade_volume NUMERIC(10,3) NOT NULL,
    quilometragem     INTEGER       NOT NULL DEFAULT 0,
    status            VARCHAR(30)   NOT NULL,
    criado_em         TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE INDEX idx_vehicle_placa
    ON vehicle (placa);


CREATE INDEX idx_vehicle_renavam
    ON vehicle (renavam);


CREATE INDEX idx_vehicle_status
    ON vehicle (status);
