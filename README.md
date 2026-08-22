# # App Rotina

Aplicação web para controle de hábitos e estudos diários, com sistema de sequência (streak), níveis e XP para engajar o uso contínuo. Feita como projeto de portfólio.

## Funcionalidades

- **Cadastro de usuário** com validação de campos (nome, e-mail, senha).
- **Cadastro de rotinas (hábitos)**: nome, descrição, frequência e se são obrigatórias ou não para manter o streak.
- **Cadastro de estudos**: matéria e status (pendente, em andamento, concluído).
- **Streak diário**: cada rotina e cada estudo tem uma sequência de dias consecutivos, calculada a partir de um histórico real de cumprimento (não apenas um contador solto).
- **Hábitos não obrigatórios** não quebram o streak caso um dia seja pulado.
- **Sistema de nível e XP** para as rotinas.
- **Dashboard** central mostrando todas as rotinas e estudos, com opção de marcar como cumprido, editar ou excluir.

## Tecnologias

- Java 21
- Spring Boot 3 (Web, Thymeleaf, Validation)
- PostgreSQL (acesso via JDBC puro, sem ORM)
- HTML + CSS
- Maven

## Arquitetura

O projeto segue a divisão em camadas **Controller → Service → DAO**:

- **Controller**: recebe as requisições HTTP e devolve a resposta (páginas ou redirects).
- **Service**: concentra a lógica de negócio, como o cálculo do streak.
- **DAO**: executa o SQL puro contra o banco de dados.

## Como rodar o projeto

### Pré-requisitos

- [JDK 21](https://adoptium.net/)
- [Maven](https://maven.apache.org/download.cgi)
- [PostgreSQL](https://www.postgresql.org/download/) rodando localmente

### 1. Clonar o repositório

```bash
git clone <url-do-seu-repositorio>
cd "App Rotina"
```

### 2. Criar o banco de dados

Crie um banco chamado `postgres` (ou ajuste o nome no `ConnectionFactory.java`) e rode o script `schema.sql` que está na raiz do projeto — ele cria todas as tabelas necessárias:

```bash
psql -U postgres -d postgres -f schema.sql
```

Ou, se preferir, abra o `schema.sql` no DBeaver/pgAdmin e execute o script inteiro.

### 3. Configurar a conexão

Confira se as credenciais em `src/main/java/org/example/ConnectionFactory.java` batem com o seu banco local:

```java
private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
private static final String USUARIO = "postgres";
private static final String SENHA = "sua_senha_aqui";
```

### 4. Rodar a aplicação

```bash
mvn spring-boot:run
```

### 5. Acessar

Com o servidor rodando, abra no navegador:

| Página | URL |
|---|---|
| Dashboard | http://localhost:8080/dashboard |
| Cadastro de usuário | http://localhost:8080/cadastro.html |
| Cadastro de rotina | http://localhost:8080/cadastro-rotina.html |
| Cadastro de estudo | http://localhost:8080/cadastro-estudo.html |

## Estrutura do projeto

```
src/main/java/org/example/
├── AppRotinaApplication.java   # ponto de entrada Spring Boot
├── ConnectionFactory.java      # conexão com o PostgreSQL
├── Rotina.java / RotinaDAO.java / RotinaController.java / RotinaService.java
├── Estudo.java / EstudoDAO.java / EstudoController.java / EstudoService.java
├── Usuario.java / UsuarioDAO.java / UsuarioController.java
├── RegistroCumprimento.java / RegistroCumprimentoDAO.java
├── RegistroEstudoDiarioDAO.java
└── DashboardController.java

src/main/resources/
├── static/             # páginas de cadastro (HTML fixo) + CSS
└── templates/          # dashboard e telas de edição (Thymeleaf)
```

## Próximos passos

- Autenticação/login de fato (hoje o cadastro de usuário existe, mas as demais telas não exigem login).
- Integração com IA para resumir conteúdos de estudo.
- Testes automatizados.

- -- Script completo de criação das tabelas do App Rotina
-- Rode no seu banco PostgreSQL antes de iniciar a aplicação

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    numero INTEGER,
    senha VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS rotinas (
    idRotina SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255),
    frequencia VARCHAR(20),
    dataDeCriacao DATE,
    obrigatorio BOOLEAN DEFAULT false,
    streakAtual INTEGER DEFAULT 0,
    melhorStreak INTEGER DEFAULT 0,
    nivel INTEGER DEFAULT 1,
    xpAtual INTEGER DEFAULT 0,
    xpParaProximoNivel INTEGER DEFAULT 100
);

CREATE TABLE IF NOT EXISTS registro_cumprimento (
    idRegistro SERIAL PRIMARY KEY,
    idRotina INTEGER NOT NULL REFERENCES rotinas(idRotina),
    data DATE NOT NULL,
    cumprido BOOLEAN DEFAULT true,
    UNIQUE (idRotina, data)
);

CREATE TABLE IF NOT EXISTS estudo (
    idEstudo SERIAL PRIMARY KEY,
    materia VARCHAR(100) NOT NULL,
    status VARCHAR(20),
    dataCriacao DATE,
    streakAtual INTEGER DEFAULT 0,
    melhorStreak INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS registro_estudo_diario (
    idRegistro SERIAL PRIMARY KEY,
    idEstudo INTEGER NOT NULL REFERENCES estudo(idEstudo),
    data DATE NOT NULL,
    UNIQUE (idEstudo, data)
);
