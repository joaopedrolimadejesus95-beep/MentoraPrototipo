# App Rotina

Aplicação web para controle de hábitos e estudos diários, com sequência de dias (streak), níveis e XP para incentivar o uso contínuo.

O streak não é um contador solto: ele é calculado a partir do histórico real de cumprimento, com um registro por dia para cada rotina e cada estudo.

![Dashboard do App Rotina](dash.png)

## Funcionalidades

- **Cadastro de usuário** com validação de nome, e-mail e senha
- **Cadastro de rotinas (hábitos)** com nome, descrição, frequência e se são obrigatórias para manter o streak
- **Cadastro de estudos** com matéria e status (pendente, em andamento, concluído)
- **Streak diário** por rotina e por estudo, calculado a partir do histórico de cumprimento
- **Hábitos não obrigatórios** não quebram o streak quando um dia é pulado
- **Níveis e XP** para as rotinas
- **Dashboard** com todas as rotinas e estudos, onde dá pra marcar como cumprido, editar ou excluir

## Tecnologias

- Java 21
- Spring Boot 3 (Web, Thymeleaf, Validation)
- PostgreSQL, com acesso via JDBC puro (sem ORM)
- HTML e CSS
- Maven

## Arquitetura

O projeto é dividido em três camadas:

```
Controller  →  Service  →  DAO  →  PostgreSQL
```

- **Controller**: recebe as requisições HTTP e devolve páginas ou redirects
- **Service**: concentra a regra de negócio, como o cálculo do streak e do XP
- **DAO**: executa o SQL contra o banco usando `PreparedStatement`

### Como o streak funciona

Cada vez que uma rotina é marcada como cumprida, é gravada uma linha em `registro_cumprimento` com a data do dia. A restrição `UNIQUE (idRotina, data)` garante que só exista um registro por rotina por dia.

O streak atual é calculado contando os dias consecutivos de cumprimento a partir de hoje, olhando para trás no histórico. O melhor streak fica salvo na própria rotina. Os estudos seguem a mesma lógica com a tabela `registro_estudo_diario`.

## Como rodar

### Pré-requisitos

- [JDK 21](https://adoptium.net/)
- [Maven](https://maven.apache.org/download.cgi)
- [PostgreSQL](https://www.postgresql.org/download/) rodando localmente

### 1. Clonar o repositório

```bash
git clone https://github.com/joaopedrolimadejesus95-beep/MentoraPrototipo.git
cd MentoraPrototipo
```

### 2. Criar as tabelas

Rode o script `schema.sql` no seu banco:

```bash
psql -U postgres -d postgres -f schema.sql
```

Ou abra o arquivo no DBeaver ou pgAdmin e execute o script inteiro.

### 3. Configurar a conexão com o banco

As credenciais são lidas de variáveis de ambiente:

| Variável      | Exemplo                                     |
| ------------- | ------------------------------------------- |
| `DB_URL`      | `jdbc:postgresql://localhost:5432/postgres` |
| `DB_USER`     | `postgres`                                  |
| `DB_PASSWORD` | a senha do seu PostgreSQL                   |

No Linux ou macOS:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/postgres
export DB_USER=postgres
export DB_PASSWORD=sua_senha
```

No Windows (PowerShell):

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/postgres"
$env:DB_USER="postgres"
$env:DB_PASSWORD="sua_senha"
```

### 4. Rodar a aplicação

```bash
mvn spring-boot:run
```

### 5. Acessar

| Página              | URL                                          |
| ------------------- | -------------------------------------------- |
| Dashboard           | <http://localhost:8080/dashboard>            |
| Cadastro de usuário | <http://localhost:8080/cadastro.html>        |
| Cadastro de rotina  | <http://localhost:8080/cadastro-rotina.html> |
| Cadastro de estudo  | <http://localhost:8080/cadastro-estudo.html> |

## Estrutura do projeto

```
src/main/java/org/example/
├── AppRotinaApplication.java    # ponto de entrada do Spring Boot
├── ConnectionFactory.java       # conexão com o PostgreSQL
├── Rotina / RotinaDAO / RotinaController / RotinaService
├── Estudo / EstudoDAO / EstudoController / EstudoService
├── Usuario / UsuarioDAO / UsuarioController
├── RegistroCumprimento / RegistroCumprimentoDAO
├── RegistroEstudoDiarioDAO
└── DashboardController

src/main/resources/
├── static/       # páginas de cadastro (HTML) e CSS
└── templates/    # dashboard e telas de edição (Thymeleaf)

schema.sql        # criação das tabelas
```

## Próximos passos

- [ ] Login e autenticação
- [ ] Vincular rotinas e estudos a cada usuário
- [ ] Guardar senhas com hash (BCrypt)
- [ ] Testes automatizados para o cálculo do streak
- [ ] Integração com IA para resumir conteúdos de estudo

---

Projeto feito para aprendizado e portfólio.
