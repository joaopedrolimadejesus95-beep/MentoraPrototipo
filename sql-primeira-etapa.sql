-- Execute este arquivo uma vez no banco PostgreSQL configurado em ConnectionFactory.

CREATE TABLE IF NOT EXISTS configuracao_estudo (
    id INTEGER PRIMARY KEY DEFAULT 1 CHECK (id = 1),
    minimo_diario_min INTEGER NOT NULL DEFAULT 15 CHECK (minimo_diario_min > 0),
    meta_diaria_min INTEGER NOT NULL DEFAULT 90 CHECK (meta_diaria_min > 0)
);

INSERT INTO configuracao_estudo (id, minimo_diario_min, meta_diaria_min)
VALUES (1, 15, 90)
ON CONFLICT (id) DO NOTHING;

CREATE TABLE IF NOT EXISTS sessao_estudo (
    id BIGSERIAL PRIMARY KEY,
    data DATE NOT NULL DEFAULT CURRENT_DATE,
    area VARCHAR(30) NOT NULL CHECK (area IN ('CARREIRA_DEV', 'FACULDADE')),
    duracao_min INTEGER NOT NULL CHECK (duracao_min > 0),
    assunto VARCHAR(160),
    registrado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sessao_estudo_data ON sessao_estudo (data);

CREATE TABLE IF NOT EXISTS sessao_treino (
    id BIGSERIAL PRIMARY KEY,
    data DATE NOT NULL DEFAULT CURRENT_DATE,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('CALISTENIA', 'CORRIDA')),
    concluida BOOLEAN NOT NULL DEFAULT FALSE,
    observacao TEXT,
    registrado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sessao_treino_data ON sessao_treino (data);

CREATE TABLE IF NOT EXISTS exercicio_realizado (
    id BIGSERIAL PRIMARY KEY,
    sessao_treino_id BIGINT NOT NULL REFERENCES sessao_treino(id) ON DELETE CASCADE,
    nome VARCHAR(120) NOT NULL,
    ordem INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS serie_realizada (
    id BIGSERIAL PRIMARY KEY,
    exercicio_realizado_id BIGINT NOT NULL REFERENCES exercicio_realizado(id) ON DELETE CASCADE,
    numero INTEGER NOT NULL CHECK (numero > 0),
    repeticoes INTEGER CHECK (repeticoes > 0),
    carga NUMERIC(7,2) CHECK (carga >= 0)
);
