# ThomPharma

Sistema de gestão para farmácia de manipulação desenvolvido em Java com JavaFX e PostgreSQL.

Projeto desenvolvido para estudo e portfólio, inspirado no contexto do meu estágio em uma farmácia de manipulação, onde tive contato com o processo de modernização de um sistema legado. Utilizei IA (Claude, da Anthropic) como ferramenta de apoio ao aprendizado — as decisões de arquitetura, escolha de tecnologias e resolução de problemas foram minhas.

---

## Tecnologias

- Java 25
- JavaFX
- PostgreSQL 16
- Maven
- Git/GitHub

---

## Funcionalidades implementadas

- Autenticação de usuários com controle de acesso
- Cadastro de clientes com suporte a médicos e prescritores
- Cadastro de fornecedores com validação de CNPJ/CPF
- Cadastro de funcionários
- Cadastro de matérias-primas com lotes, validade e fornecedor
- Máscaras automáticas de formatação (CPF, CNPJ, telefone, CEP, data)
- Interface fullscreen com navegação entre módulos
- Conexão segura com banco via arquivo de configuração externo

---

## Módulos em desenvolvimento

- Receitas e fórmulas
- Orçamentos e pedidos
- Cálculos automáticos para florais e homeopatia
- Geração de rótulos
- Relatórios

---

## Telas

### Login
![Login](screenshots/tela%20login.png)

### Menu Principal
![Menu Principal](screenshots/menu%20principal.png)

### Cadastro de Matérias-Primas
![Matérias-Primas](screenshots/cadastro%20materias%20primas.png)

### Cadastro de Clientes
![Clientes](screenshots/cadastro%20clientes.png)

---

## Como executar

1. Clone o repositório
2. Copie `src/main/resources/config.properties.exemplo` para `src/main/resources/config.properties`
3. Preencha com seus dados do PostgreSQL
4. Crie o banco de dados e execute os scripts SQL disponíveis em `docs/`
5. Execute com Maven: `mvn javafx:run`

---

## Status

🚧 Em desenvolvimento ativo

---

## Autor

Thomas Martin Bueno de Moraes  
Estudante de Engenharia da Computação — UNAERP  
[LinkedIn](https://www.linkedin.com/in/thomasmbmoraes/
[GitHub](https://github.com/thomasmbmoraes)