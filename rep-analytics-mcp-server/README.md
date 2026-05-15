# REP Analytics MCP Server

Servidor MCP local para responder perguntas de negocio sobre funcionarios e banco de horas usando dados simulados ou, depois, APIs reais.

## Objetivo

Este projeto foi criado a partir do padrao do `spring-ai-mcp-server`, mas focado no dominio de ponto:

- API 1: funcionarios (`nome`, `matricula`, `dataEntrada`, `aniversario`)
- API 2: banco de horas (`matricula`, `mes`, `saldoMinutos`)

O MCP expoe tools genericas derivadas das APIs-base para que um cliente com LLM consiga responder perguntas como:

- `Qual funcionario no mes de abril tem mais banco de horas?`
- `Qual funcionario tem mais tempo de casa?`

## Como a simulacao funciona

As APIs estao simuladas por arquivos JSON em `src/main/resources/mock-api/`.

- `employees.json`
- `hour-banks.json`

O servidor le esses arquivos em runtime e monta as respostas como se estivesse consultando integracoes externas. Isso permite validar o fluxo MCP + LLM antes de conectar APIs reais.

## Registro no Codex

Exemplo de configuracao em `C:\Users\Lucas\.codex\config.toml`:

```toml
[mcp_servers.rep-analytics]
command = "java"
args = ["-jar", 'H:\Development\Workspace\chrosyn-soft\tools\rep-analytics-mcp-server\target\rep-analytics-mcp-server-0.0.1-SNAPSHOT.jar']
```

## Build

```powershell
.\mvnw.cmd test
.\mvnw.cmd -DskipTests package
```

## Tools disponiveis

- `repApisSimuladasVisaoGeral()`: descreve os contratos simulados e exemplos de payload.
- `repListarFuncionarios()`: lista todos os funcionarios disponiveis no mock.
- `repBuscarFuncionarios(matricula, anoAdmissao, mesAniversario)`: busca funcionarios por matricula, ano de admissao e/ou mes de aniversario.
- `repListarLancamentosBancoHoras(matricula, periodo)`: lista lancamentos de banco de horas por matricula e/ou periodo.

## Formatos aceitos

Mes:

- `2026-04`
- `04/2026`
- `abril`
- `abr`

Data de referencia:

- `2026-05-15`

Se o usuario informar apenas `abril`, o MCP assume `abril de 2026`, com base no ano default configurado no servidor.

## Evolucao para APIs reais

O desenho separa porta de funcionarios e porta de banco de horas. A interpretacao das perguntas fica com a LLM cliente; o MCP nao faz parser de linguagem natural.

Para sair do mock:

1. implemente um adapter HTTP para `PortaFuncionarios`;
2. implemente um adapter HTTP para `PortaBancoHoras`;
3. mantenha as tools MCP inalteradas.

Assim a LLM continua usando as mesmas capabilities, com troca apenas na infraestrutura.
