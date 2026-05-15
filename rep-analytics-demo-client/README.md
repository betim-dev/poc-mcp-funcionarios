# Cliente Demo REP Analytics

Cliente de terminal para conversar com o MCP `rep-analytics-mcp-server` usando um LLM e tool calling via MCP.

## O que este projeto faz

Este cliente:

- sobe uma conexao MCP via `STDIO` para o servidor `rep-analytics-mcp-server`;
- injeta as tools MCP no `ChatClient` do Spring AI;
- abre um chat no terminal para perguntas em portugues;
- permite trocar facilmente o LLM por um provider mais economico ou por um modelo local.

## Como rodar

1. Gere o JAR do MCP:

```powershell
cd H:\Development\Workspace\chrosyn-soft\tools\rep-analytics-mcp-server
.\mvnw.cmd -DskipTests package
```

2. Escolha um provider LLM e configure as variaveis de ambiente.

Exemplo com endpoint OpenAI-compatible:

```powershell
$env:LLM_API_KEY="sua-chave"
$env:LLM_BASE_URL="https://api.openai.com"
$env:LLM_MODEL="gpt-4o-mini"
```

3. Rode o cliente:

```powershell
cd H:\Development\Workspace\chrosyn-soft\tools\rep-analytics-demo-client
.\mvnw.cmd spring-boot:run
```

Atalho no Windows:

```powershell
cd H:\Development\Workspace\chrosyn-soft\tools\rep-analytics-demo-client
.\run-demo-client.ps1
```

## Como conversar

No terminal, digite perguntas como:

- `Qual funcionario no mes de abril tem mais banco de horas?`
- `Qual funcionario tem mais banco de horas em 2026?`
- `Quantos funcionarios foram contratados em 2021?`
- `Quais funcionarios fazem aniversario em maio?`
- `Qual funcionario tem mais tempo de casa?`
- `Liste os funcionarios simulados`
- `Mostre o banco de horas de abril`

Comandos locais do terminal:

- `ajuda`: mostra exemplos
- `tools`: lista as tools MCP descobertas
- `sair`: encerra o chat

## Exemplo de retorno

```text
Funcionario com mais banco de horas em abril de 2026 (ano assumido pelo servidor):
- Nome: Bruno Henrique Lima
- Matricula: 1002
- Saldo: 18h30
- Interpretacao: Bruno Henrique Lima lidera o banco de horas no mes analisado.
```

## Integrando LLMs mais economicos

Este cliente usa o starter `spring-ai-starter-model-openai`, mas isso nao significa usar somente OpenAI. O ponto importante e que o provider exponha uma API compativel com OpenAI ou tenha starter proprio no Spring AI.

### Opcao 1: OpenAI-compatible

Funciona bem quando o provider aceita `base-url`, `api-key` e `model`.

Configuracao generica:

```powershell
$env:LLM_API_KEY="sua-chave"
$env:LLM_BASE_URL="https://seu-endpoint-openai-compatible"
$env:LLM_MODEL="seu-modelo"
```

Essa abordagem costuma ser a mais simples para trocar entre providers sem mudar o codigo.

### Opcao 2: Ollama local

Para rodar localmente sem custo por chamada:

```powershell
$env:LLM_API_KEY="ollama"
$env:LLM_BASE_URL="http://localhost:11434"
$env:LLM_MODEL="mistral"
```

Voce tambem pode testar modelos menores locais, por exemplo variantes pequenas de `qwen`, `mistral` ou `deepseek`, dependendo do que tiver carregado no Ollama.

### Opcao 3: providers com starter dedicado

Se preferir usar um starter especifico do Spring AI em vez de OpenAI-compatible, crie um profile ou troque a dependencia do cliente. O MCP nao muda; apenas o `ChatModel` muda.

## Fontes oficiais usadas para esta integracao

- Spring AI MCP Client Boot Starter:
  `https://docs.spring.io/spring-ai/reference/api/mcp/mcp-client-boot-starter-docs.html`
- Spring AI Tool Calling / ChatClient:
  `https://docs.spring.io/spring-ai/reference/api/tools.html`
- Spring AI OpenAI Chat:
  `https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html`
- Spring AI Ollama Chat:
  `https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html`
- Spring AI Groq Chat:
  `https://docs.spring.io/spring-ai/reference/api/chat/groq-chat.html`
- Spring AI Mistral AI Chat:
  `https://docs.spring.io/spring-ai/reference/api/chat/mistralai-chat.html`

## Como integrar outro provider

1. Troque `LLM_BASE_URL`.
2. Troque `LLM_API_KEY`.
3. Troque `LLM_MODEL`.
4. Rode o cliente novamente.

Se o provider for OpenAI-compatible, o codigo do cliente nao precisa mudar.

## Observacoes

- O caminho do JAR do MCP esta configurado relativamente em `src/main/resources/mcp-servers.json`.
- Segundo a documentacao do Spring AI MCP Client, caminhos relativos no `STDIO` sao resolvidos a partir do diretório de trabalho da aplicacao.
- Se quiser apontar para outro JAR, ajuste o arquivo `mcp-servers.json`.
