-- 1. Categorias
CREATE TABLE categorias (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    tipo VARCHAR(20) NOT NULL -- 'RECEITA' ou 'DESPESA'
);

-- 2. Transações
CREATE TABLE transacoes (
    id SERIAL PRIMARY KEY,
    descricao VARCHAR(200),
    valor NUMERIC(19, 2) NOT NULL, -- 19 dígitos, 2 decimais
    tipo VARCHAR(20) NOT NULL, -- 'RECEITA' ou 'DESPESA'
    data_movimento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    categoria_id INTEGER NOT NULL,
    CONSTRAINT fk_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

-- 3. Alunos
CREATE TABLE alunos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    whatsapp VARCHAR(20),
    data_nascimento DATE NOT NULL,
    dia_preferencia_pagamento INTEGER NOT NULL
);

-- 4. AlunosPagamento
CREATE TABLE alunos_pagamentos (
    id SERIAL PRIMARY KEY,
    aluno_id INTEGER NOT NULL,

    -- Dados da Cobrança (Previsão)
    data_vencimento DATE NOT NULL,
    valor_cobrado NUMERIC(19, 2) NOT NULL,

    -- Dados da Baixa (Realização) - NULLABLE!
    data_pagamento DATE,
    valor_pago NUMERIC(19, 2),

    -- A Conexão com o Caixa
    transacao_id INTEGER UNIQUE, -- Unique para garantir 1 pagamento = 1 transação

    CONSTRAINT fk_aluno FOREIGN KEY (aluno_id) REFERENCES alunos(id),
    CONSTRAINT fk_transacao FOREIGN KEY (transacao_id) REFERENCES transacoes(id)
);