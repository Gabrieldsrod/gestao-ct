# Guia de Execução - Gestão CT

## Pré-requisitos

1. **Java 21** instalado
2. **PostgreSQL** rodando localmente na porta 5432
3. **Maven** instalado (ou use `mvnw` incluído)

## Configuração do Banco de Dados

### 1. Criar o banco e usuário PostgreSQL

```sql
-- Conectar como superuser (postgres)
CREATE DATABASE gestao_ct_db;
CREATE USER gestao_user WITH PASSWORD 'senha_segura';
GRANT ALL PRIVILEGES ON DATABASE gestao_ct_db TO gestao_user;
```

### 2. Definir credenciais (opções)

**Opção A: Usar valores padrão (postgres/postgres)**
- O `application.properties` usa valores padrão: `DB_USER=postgres` e `DB_PASSWORD=postgres`

**Opção B: Definir variáveis de ambiente (recomendado)**

No PowerShell:
```powershell
$env:DB_USER="seu_usuario"
$env:DB_PASSWORD="sua_senha"
```

No CMD:
```cmd
set DB_USER=seu_usuario
set DB_PASSWORD=sua_senha
```

**Opção C: Editar arquivo `.env`**
```
DB_USER=postgres
DB_PASSWORD=postgres
```

## Executar a Aplicação

### Via JAR (após `mvn package`)

```bash
java -jar target/gestao-ct-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

### Via Maven direto

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Via IDE (IntelliJ)
1. Abra `GestaoCtApplication.java`
2. Clique no botão verde ▶️ ao lado da classe
3. (Opcional) Defina variáveis de ambiente em Run → Edit Configurations

## Perfis de Configuração

- **dev** (desenvolvimento): Cria/recria tabelas automaticamente (`create-drop`)
- **prod** (produção): Valida apenas estrutura existente (`validate`)
- **padrão**: Usa `validate` (requer tabelas existentes)

## Resolução de Problemas

### Erro: "password authentication failed for user"
- Verifique se PostgreSQL está rodando
- Confirme credenciais em `application.properties` ou `.env`
- Teste conexão: `psql -U postgres -h localhost`

### Erro: "FATAL: database does not exist"
- Crie o banco: `CREATE DATABASE gestao_ct_db;`

### Erro: "Unable to build Hibernate SessionFactory"
- Verifique conexão com PostgreSQL
- Confirme que o banco existe
- Consulte logs completos do Spring Boot

## Endpoints Disponíveis

- `GET  /v1/api/pagamentos` - Listar pagamentos
- `POST /v1/api/pagamentos/gerar-teste` - Gerar dados de teste
- (Outros conforme desenvolvidos)

## Logs

Logs são exibidos no console com SQL formatado quando `spring.jpa.show-sql=true`.

