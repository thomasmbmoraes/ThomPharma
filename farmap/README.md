# ThomPharma

Sistema de gerenciamento para farmácias de manipulação, desenvolvido em Java com JavaFX e PostgreSQL.

Projeto desenvolvido para estudo e portfólio, inspirado no contexto do meu estágio em uma farmácia de manipulação, onde tive contato com o processo de modernização de um sistema legado. Utilizei IA (Claude, da Anthropic) como ferramenta de apoio ao aprendizado — as decisões de arquitetura, escolha de tecnologias e resolução de problemas foram minhas.

![Java](https://img.shields.io/badge/Java-11+-orange?logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-13-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-336791?logo=postgresql)
![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?logo=apachemaven)

---

## Funcionalidades

| Módulo | Descrição |
|--------|-----------|
| **Login** | Autenticação com bcrypt, controle de acesso por perfil (Admin/Operador) |
| **Matérias-Primas** | Cadastro com controle de estoque por lotes, alertas de mínimo e crítico |
| **Clientes** | Cadastro completo com endereço, múltiplos telefones e desconto individual |
| **Prescritores** | Médicos, dentistas, veterinários com número de registro |
| **Receitas** | Fórmulas farmacêuticas reutilizáveis em pedidos |
| **Pedidos** | Ordens de manipulação vinculando cliente + prescritor + receita |
| **Rótulos** | Geração automática ao produzir pedidos, código diário sequencial, impressão direta |
| **Calculadora** | Floral, Homeopatia Líquida, Glóbulos e Dose Única |
| **Relatórios** | Pedidos por período, estoque, rótulos emitidos, ranking de clientes |
| **Usuários** | Cadastro com senhas em bcrypt, perfis Admin/Operador |
| **Fornecedores** | Cadastro com CNPJ/CPF e validação de máscara |
| **Funcionários** | Cadastro completo de colaboradores |

---

## Segurança

- Senhas armazenadas com **bcrypt** (jBCrypt 0.4, salt automático por registro)
- Migração automática de senhas legadas para bcrypt no primeiro login
- Controle de acesso por perfil: Admin acessa tudo; Operador acessa apenas módulos operacionais
- Todos os SQLs usam `PreparedStatement` (sem SQL Injection)
- Credenciais do banco em `config.properties` (gitignored, nunca no código)

---

## Tecnologias

- **Java 11** com sistema de módulos JPMS (`module-info.java`)
- **JavaFX 13** — interface gráfica com FXML e CSS customizado
- **PostgreSQL** via JDBC com `PreparedStatement` em todas as queries
- **jBCrypt 0.4** — hash de senhas
- **Maven 3.9** — build e gerenciamento de dependências
- **JDK 25** (Temurin) — bundled no pacote de distribuição Windows

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

### Pré-requisitos
- PostgreSQL 14+ instalado e rodando
- Java 11+ (ou usar o JDK bundled em `pendrive/jdk/`)
- Maven 3.9+

### 1. Configuração do banco

Execute o script SQL de criação das tabelas disponível em `docs/schema.sql`.

### 2. Arquivo de configuração

```bash
cp config.properties.template src/main/resources/thompharma/config.properties
```

Edite o arquivo com seus dados:

```properties
db.url.local=jdbc:postgresql://localhost:5432/DB_FARMAP
db.url.remota=jdbc:postgresql://SEU_SERVIDOR:5432/DB_FARMAP
db.usuario=SEU_USUARIO
db.senha=SUA_SENHA
```

### 3. Compilar e executar

```bash
mvn clean compile
mvn javafx:run
```

### 4. Distribuição Windows (sem JDK instalado)

```powershell
.\empacota.ps1
pendrive\ThomPharma.bat
```

O pacote `pendrive/` inclui JDK 25, todas as dependências e um `config.properties` local com prioridade sobre o interno.

---

## Estrutura do Projeto

```
farmap/
├── src/main/
│   ├── java/
│   │   ├── module-info.java
│   │   └── thompharma/
│   │       ├── App.java               # Ponto de entrada + estado global de sessão
│   │       ├── Conexao.java           # Gerenciador de conexão (local → remoto)
│   │       ├── Mascara.java           # Utilitários de formatação de campos
│   │       ├── modelo/                # POJOs: Cliente, Pedido, Receita, Rotulo...
│   │       └── telas/                 # Controllers JavaFX (um por tela)
│   └── resources/thompharma/
│       ├── *.fxml                     # Layouts das telas
│       ├── thompharma.css             # Tema Tokyo Night
│       └── config.properties          # Credenciais (gitignored)
├── pendrive/                          # Distribuição standalone Windows
│   ├── jdk/                          # JDK 25 bundled
│   ├── deps/                         # JavaFX win + PostgreSQL + jBCrypt
│   └── ThomPharma.bat                # Launcher
├── config.properties.template         # Template seguro para git
├── empacota.ps1                       # Script de build para distribuição
└── pom.xml
```

---

## Perfis de Acesso

| Módulo | Admin | Operador |
|--------|-------|----------|
| Matérias-Primas | ✅ | ❌ |
| Clientes | ✅ | ✅ |
| Prescritores | ✅ | ✅ |
| Receitas | ✅ | ✅ |
| Calculadora | ✅ | ✅ |
| Pedidos | ✅ | ✅ |
| Tabelas/Usuários | ✅ | ❌ |
| Relatórios | ✅ | ❌ |
| Rótulos | ✅ | ✅ |

---

## Autor

**Thomas Martin Bueno de Moraes**  
Estudante de Engenharia da Computação — UNAERP  
[LinkedIn](https://www.linkedin.com/in/thomasmbmoraes/) | [GitHub](https://github.com/thomasmbmoraes)
