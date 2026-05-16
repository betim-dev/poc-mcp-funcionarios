package br.com.chrosyn.tools.repmcp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.chrosyn.tools.repmcp.adapter.mcp.RepAnalyticsTools;

@SpringBootTest(classes = RepAnalyticsMcpServerApplication.class)
class RepAnalyticsMcpServerApplicationTests {

	@Autowired
	private RepAnalyticsTools repAnalyticsTools;

	@Test
	void shouldListAllEmployees() {
		String answer = repAnalyticsTools.repListarFuncionarios();

		assertThat(answer).contains("Lucas Betim");
		assertThat(answer).contains("Diego Sureck");
	}

	@Test
	void shouldFilterEmployeesByHireYear() {
		String answer = repAnalyticsTools.repBuscarFuncionarios(null, 2026, null);

		assertThat(answer).contains("Ricardo Oliveira");
		assertThat(answer).contains("Juliana Costa");
		assertThat(answer).contains("Juliana Martins");
	}

	@Test
	void shouldFilterEmployeesByBirthdayMonth() {
		String answer = repAnalyticsTools.repBuscarFuncionarios(null, null, "maio");

		assertThat(answer).contains("Patrícia Souza");
		assertThat(answer).contains("05/05/1991");
	}

	@Test
	void shouldListHourBankEntriesForMonth() {
		String answer = repAnalyticsTools.repListarLancamentosBancoHoras(null, "abril");

		assertThat(answer).contains("2026-04");
		assertThat(answer).contains("André Souza");
	}

	@Test
	void shouldListHourBankEntriesForYearAndRegistration() {
		String answer = repAnalyticsTools.repListarLancamentosBancoHoras("1002", "2026");

		assertThat(answer).contains("Bruno Henrique Lima");
		assertThat(answer).contains("2026-01");
		assertThat(answer).contains("2026-04");
		assertThat(answer).contains("2026-05");
	}

}
