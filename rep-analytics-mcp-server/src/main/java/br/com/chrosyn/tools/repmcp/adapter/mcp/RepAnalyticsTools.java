package br.com.chrosyn.tools.repmcp.adapter.mcp;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import br.com.chrosyn.tools.repmcp.application.RepAnalyticsService;

@Service
public class RepAnalyticsTools {

	private final RepAnalyticsService analyticsService;

	public RepAnalyticsTools(RepAnalyticsService analyticsService) {
		this.analyticsService = analyticsService;
	}

	@Tool(description = "Descreve as APIs simuladas de funcionarios e banco de horas consumidas por este MCP.")
	public String repApisSimuladasVisaoGeral() {
		return analyticsService.apisSimuladasVisaoGeral();
	}

	@Tool(description = "Lista os funcionarios da empresa, incluindo detalhes como matricula, nome, data de admissao e data de nascimento (aniversario).")
	public String repListarFuncionarios() {
		return analyticsService.listarFuncionarios();
	}

	@Tool(description = "Busca funcionarios usando filtros opcionais baseados na API de funcionarios. Use matricula para busca exata, anoAdmissao para ano de admissao e mesAniversario para o mes do aniversario.")
	public String repBuscarFuncionarios(String matricula, Integer anoAdmissao, String mesAniversario) {
		return analyticsService.buscarFuncionarios(matricula, anoAdmissao, mesAniversario);
	}

	@Tool(description = "Lista lancamentos de banco de horas usando filtros opcionais baseados na API de banco de horas. Use matricula para busca exata e periodo para ano ou mes. periodo aceita 2026, 2026-04, 04/2026, abril e abr.")
	public String repListarLancamentosBancoHoras(String matricula, String periodo) {
		return analyticsService.listarLancamentosBancoHoras(matricula, periodo);
	}

}
