# POC MCP Funcionarios

POC para demonstrar um fluxo `LLM + MCP` no dominio de funcionarios e banco de horas.

## Objetivo

O projeto mostra como responder perguntas em linguagem natural a partir de duas APIs-base:

- funcionarios: `matricula`, `nome`, `dataAdmissao`, `aniversario`
- banco de horas: `matricula`, `mesReferencia`, `saldoMinutos`

O MCP nao interpreta perguntas por regras hardcoded. Ele expoe tools genericas de consulta, e a LLM decide como usar essas tools para montar a resposta.

## Estrutura

- [rep-analytics-mcp-server](H:\Development\Workspace\chrosyn-soft\tools\rep-analytics-mcp-server\README.md)
  MCP server Spring AI via STDIO com tools genericas sobre funcionarios e banco de horas.
- [rep-analytics-demo-client](H:\Development\Workspace\chrosyn-soft\tools\rep-analytics-demo-client\README.md)
  Cliente de terminal com LLM + MCP e auditoria das chamadas de tool.

## Arquitetura

1. O cliente recebe a pergunta do usuario.
2. A LLM interpreta a pergunta com base nas tools MCP disponiveis.
3. O MCP consulta as fontes simuladas ou futuras APIs reais.
4. A LLM consolida o retorno e responde em linguagem natural.

## Tools expostas pelo MCP

- `repApisSimuladasVisaoGeral()`
- `repListarFuncionarios()`
- `repBuscarFuncionarios(matricula, anoAdmissao, mesAniversario)`
- `repListarLancamentosBancoHoras(matricula, periodo)`

## Como rodar

### 1. Empacotar o MCP

```powershell
cd H:\Development\Workspace\chrosyn-soft\tools\rep-analytics-mcp-server
.\mvnw.cmd clean package
```

### 2. Configurar o LLM

Exemplo OpenAI:

```powershell
$env:LLM_API_KEY="sua-chave"
$env:LLM_BASE_URL="https://api.openai.com"
$env:LLM_MODEL="gpt-4o-mini"
```

Exemplo Ollama local:

```powershell
$env:LLM_API_KEY="ollama"
$env:LLM_BASE_URL="http://localhost:11434"
$env:LLM_MODEL="mistral"
```

### 3. Rodar o cliente demo

```powershell
cd H:\Development\Workspace\chrosyn-soft\tools\rep-analytics-demo-client
.\run-demo-client.ps1
```

## Auditoria

O cliente imprime logs no formato:

```text
[AUDITORIA] tool.inicio nome=...
[AUDITORIA] tool.fim nome=...
```

Isso permite validar quais tools a LLM chamou para responder cada pergunta.

## Evolucao para APIs reais

Troque apenas os adaptadores simulados:

- [AdaptadorSimuladoFuncionarios.java](H:\Development\Workspace\chrosyn-soft\tools\rep-analytics-mcp-server\src\main\java\br\com\chrosyn\tools\repmcp\AdaptadorSimuladoFuncionarios.java)
- [AdaptadorSimuladoBancoHoras.java](H:\Development\Workspace\chrosyn-soft\tools\rep-analytics-mcp-server\src\main\java\br\com\chrosyn\tools\repmcp\AdaptadorSimuladoBancoHoras.java)

As portas de dominio permanecem:

- [PortaFuncionarios.java](H:\Development\Workspace\chrosyn-soft\tools\rep-analytics-mcp-server\src\main\java\br\com\chrosyn\tools\repmcp\PortaFuncionarios.java)
- [PortaBancoHoras.java](H:\Development\Workspace\chrosyn-soft\tools\rep-analytics-mcp-server\src\main\java\br\com\chrosyn\tools\repmcp\PortaBancoHoras.java)

## Validacao atual

- MCP server: testes passando
- Cliente demo: testes passando
- Fluxo de perguntas em terminal: validado
