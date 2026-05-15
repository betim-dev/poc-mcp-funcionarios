$ErrorActionPreference = "Stop"

if (-not $env:LLM_API_KEY) {
    Write-Host "Defina a variavel LLM_API_KEY antes de rodar o cliente."
    Write-Host 'Exemplo: $env:LLM_API_KEY="sua-chave"'
    exit 1
}

if (-not $env:LLM_BASE_URL) {
    $env:LLM_BASE_URL = "https://api.openai.com"
}

if (-not $env:LLM_MODEL) {
    $env:LLM_MODEL = "gpt-4o-mini"
}

Write-Host "Subindo cliente demo com:"
Write-Host "LLM_BASE_URL=$env:LLM_BASE_URL"
Write-Host "LLM_MODEL=$env:LLM_MODEL"

& ".\mvnw.cmd" spring-boot:run
