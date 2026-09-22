-- Rode isso no DBeaver antes de testar (não dá erro se já existir)

ALTER TABLE estudo ADD COLUMN IF NOT EXISTS streakAtual INTEGER DEFAULT 0;
ALTER TABLE estudo ADD COLUMN IF NOT EXISTS melhorStreak INTEGER DEFAULT 0;

CREATE TABLE IF NOT EXISTS registro_estudo_diario (
    idRegistro SERIAL PRIMARY KEY,
    idEstudo INTEGER NOT NULL REFERENCES estudo(idEstudo),
    data DATE NOT NULL,
    UNIQUE (idEstudo, data)
);
