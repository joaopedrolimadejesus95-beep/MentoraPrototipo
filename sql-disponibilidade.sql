-- Execute uma vez no mesmo banco usado pelo aplicativo.
CREATE TABLE IF NOT EXISTS disponibilidade_semanal (
    id BIGSERIAL PRIMARY KEY,
    dia_semana VARCHAR(12) NOT NULL CHECK (dia_semana IN ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY')),
    inicio TIME NOT NULL,
    fim TIME NOT NULL,
    CHECK (fim > inicio),
    UNIQUE (dia_semana, inicio, fim)
);
CREATE TABLE IF NOT EXISTS ajuste_disponibilidade_dia (
    data DATE PRIMARY KEY,
    minutos_disponiveis INTEGER NOT NULL CHECK (minutos_disponiveis BETWEEN 1 AND 720),
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
