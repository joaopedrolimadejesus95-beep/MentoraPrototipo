-- Execute uma vez no banco do aplicativo.
CREATE TABLE IF NOT EXISTS plano_treino (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(80) NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('CALISTENIA', 'CORRIDA')),
    duracao_estimada_min INTEGER NOT NULL CHECK (duracao_estimada_min BETWEEN 1 AND 360),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS exercicio_plano (
    id BIGSERIAL PRIMARY KEY,
    plano_treino_id BIGINT NOT NULL REFERENCES plano_treino(id) ON DELETE CASCADE,
    nome VARCHAR(120) NOT NULL,
    ordem INTEGER NOT NULL
);

ALTER TABLE sessao_treino
    ADD COLUMN IF NOT EXISTS plano_treino_id BIGINT REFERENCES plano_treino(id) ON DELETE SET NULL;
