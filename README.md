# 🏋️‍♂️ Gestão CT - ERP para Centros de Treinamento

## 📌 Sobre o Projeto

O **Gestão CT** é um sistema completo (**End-to-End**) desenvolvido para resolver um problema real de negócio: a administração de alunos, controle de dependentes e gestão financeira de um Centro de Treinamento.

Diferente de sistemas de CRUD simples, esta aplicação foca fortemente na consistência de dados e regras de negócio complexas, garantindo que o fluxo financeiro e os vínculos familiares (planos de casal/dependentes) se mantenham íntegros através de rotinas transacionais robustas no backend.

## ✨ Principais Funcionalidades e Regras de Negócio

- **Gestão de vínculos reflexivos:** sistema avançado de dependentes, onde um aluno (titular) pode gerenciar múltiplos dependentes na mesma estrutura de dados, refletindo planos familiares.
- **Transações seguras (`@Transactional`):** atualizações em cascata que garantem a integridade do banco. Inativar ou atualizar um plano de um titular reflete automaticamente nos seus dependentes e nas cobranças pendentes.
- **Blindagem de regras de negócio:**
	- impedimento de inativação ou downgrade de titulares que possuam dependentes ativos;
	- promoção automática de dependentes a titulares em caso de separação de planos;
	- sincronização automática de boletos pendentes quando há mudança de plano no meio do ciclo.
- **API RESTful rica:** retorno de dados estruturados utilizando Nested Objects e o padrão DTO (Data Transfer Object) implementado com Java Records para maior performance e imutabilidade.
- **Frontend reativo e tipado:** interface moderna e amigável construída com React e TypeScript, utilizando validações assíncronas e tratamento de erros visuais (feedback em tempo real para o usuário).

## 🛠️ Tecnologias Utilizadas

### Backend

- **Linguagem:** Java 21
- **Framework:** Spring Boot
- **Persistência:** Spring Data JPA / Hibernate
- **Padrões de projeto:** REST, DTO Pattern (Records), Layered Architecture (Controller, Service, Repository), tratamento global de exceções
- **Banco de dados:** `PostgreSQL`

### Frontend

- **Linguagem:** TypeScript
- **Biblioteca:** React (Vite)
- **Roteamento:** TanStack Router (Type-safe routing)
- **Estilização:** Tailwind CSS + Shadcn Ui Graphs + Radix UI + Lucide Icons
- **Gerenciamento de formulários:** React Hook Form + Zod (validação de schemas)
- **Gerenciamento de estado/requisições:** custom hooks (`useFetch`, etc.)

## 🚀 Arquitetura e Boas Práticas

Para recrutadores e engenheiros revisando este código, os seguintes pontos da arquitetura se destacam:

- **Separação de responsabilidades (SRP):** serviços isolados (`MemberService`, `PaymentService`) se comunicando de forma limpa, evitando God Classes.
- **Edge cases tratados:** prevenção de "dependentes fantasmas" e "cobranças defasadas" através de validações rígidas na camada de serviço, lançando exceções de negócio customizadas (`BusinessRuleException`).
- **Performance no JPA:** utilização de Lazy Loading e Dirty Checking do Hibernate para atualizações em lote de dependentes sem sobrecarregar o banco com queries desnecessárias.
- **Type safety de ponta a ponta:** tipagem estrita no frontend (interfaces mapeando exatamente os Records do Java), evitando erros de `undefined` em tempo de execução.

## 📸 Telas do Sistema



## ⚙️ Como Executar o Projeto

```bash
# Clone o repositório
git clone https://github.com/Gabrieldsrod/gestao-ct.git

# Executar o Backend (Spring Boot)
cd backend
./mvnw spring-boot:run

# Executar o Frontend (React/Vite)
cd ../frontend
npm install
npm run dev
```

> No Windows, se necessário, use `mvnw.cmd` no lugar de `./mvnw`.

## 🌍 Conecte-se comigo

Buscando minha primeira oportunidade como Desenvolvedor Backend Estagiário/Júnior e sempre aberto a trocar ideias!

📧 **E-mail:** [gabrieldsrodrigues19@gmail.com]
💼 **LinkedIn:** [gabrieldsrod](https://www.linkedin.com/in/gabrieldsrod/)
💻 **GitHub:** [Gabrieldsrod](https://github.com/Gabrieldsrod)