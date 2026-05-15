package br.com.chrosyn.tools.repmcp;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RepAnalyticsService {

	private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final Pattern ANO_MES = Pattern.compile("(20\\d{2})[-/](0[1-9]|1[0-2])");
	private static final Pattern MES_ANO = Pattern.compile("(0[1-9]|1[0-2])/(20\\d{2})");

	private static final Map<String, Integer> MESES = new LinkedHashMap<>();

	static {
		MESES.put("janeiro", 1);
		MESES.put("jan", 1);
		MESES.put("fevereiro", 2);
		MESES.put("fev", 2);
		MESES.put("marco", 3);
		MESES.put("mar", 3);
		MESES.put("abril", 4);
		MESES.put("abr", 4);
		MESES.put("maio", 5);
		MESES.put("mai", 5);
		MESES.put("junho", 6);
		MESES.put("jun", 6);
		MESES.put("julho", 7);
		MESES.put("jul", 7);
		MESES.put("agosto", 8);
		MESES.put("ago", 8);
		MESES.put("setembro", 9);
		MESES.put("set", 9);
		MESES.put("outubro", 10);
		MESES.put("out", 10);
		MESES.put("novembro", 11);
		MESES.put("nov", 11);
		MESES.put("dezembro", 12);
		MESES.put("dez", 12);
	}

	private final PortaFuncionarios portaFuncionarios;
	private final PortaBancoHoras portaBancoHoras;
	private final int anoPadrao;

	public RepAnalyticsService(
			PortaFuncionarios portaFuncionarios,
			PortaBancoHoras portaBancoHoras,
			@Value("${rep.analytics.default-year}") int anoPadrao) {
		this.portaFuncionarios = portaFuncionarios;
		this.portaBancoHoras = portaBancoHoras;
		this.anoPadrao = anoPadrao;
	}

	public String apisSimuladasVisaoGeral() {
		return """
				APIs simuladas consumidas por este MCP:

				1. Funcionarios
				- Campos: matricula, nome, dataAdmissao, aniversario
				- Exemplo: {"matricula":"1001","nome":"Lucas Betim","dataAdmissao":"2015-03-16","aniversario":"1988-04-12"}

				2. Banco de horas
				- Campos: matricula, mesReferencia, saldoMinutos
				- Exemplo: {"matricula":"1002","mesReferencia":"2026-04","saldoMinutos":1110}

				O MCP expone apenas tools genericas de consulta. A interpretacao da pergunta fica com a LLM cliente.
				""";
	}

	public String listarFuncionarios() {
		return formatarFuncionarios(portaFuncionarios.listarFuncionarios(), "Funcionarios simulados:");
	}

	public String buscarFuncionarios(String matricula, Integer anoAdmissao, String mesAniversario) {
		Integer valorMesAniversario = mesAniversario == null || mesAniversario.isBlank()
				? null
				: resolverMes(mesAniversario).valor().getMonthValue();

		List<FuncionarioRegistro> funcionarios = portaFuncionarios.listarFuncionarios().stream()
				.filter(funcionario -> matricula == null || matricula.isBlank() || funcionario.matricula().equals(matricula))
				.filter(funcionario -> anoAdmissao == null || funcionario.dataAdmissao().getYear() == anoAdmissao)
				.filter(funcionario -> valorMesAniversario == null || funcionario.aniversario().getMonthValue() == valorMesAniversario)
				.sorted(Comparator.comparing(FuncionarioRegistro::nome))
				.toList();

		if (funcionarios.isEmpty()) {
			return "Nenhum funcionario encontrado para os filtros informados.";
		}

		return formatarFuncionarios(funcionarios, "Funcionarios encontrados:");
	}

	public String listarLancamentosBancoHoras(String matricula, String periodo) {
		PeriodoResolvido periodoResolvido = resolverPeriodo(periodo);
		Map<String, FuncionarioRegistro> funcionariosPorMatricula = funcionariosPorMatricula();

		List<String> linhas = portaBancoHoras.listarLancamentos().stream()
				.filter(lancamento -> matricula == null || matricula.isBlank() || lancamento.matricula().equals(matricula))
				.filter(lancamento -> correspondeAoPeriodo(lancamento.mesReferencia(), periodoResolvido))
				.sorted(Comparator.comparing(BancoHorasRegistro::mesReferencia).thenComparing(BancoHorasRegistro::matricula))
				.map(lancamento -> {
					FuncionarioRegistro funcionario = funcionariosPorMatricula.get(lancamento.matricula());
					String nomeFuncionario = funcionario == null ? "Matricula " + lancamento.matricula() : funcionario.nome();
					return "%s | %s | referencia %s | saldo %s".formatted(
							lancamento.matricula(),
							nomeFuncionario,
							lancamento.mesReferencia(),
							formatarMinutos(lancamento.saldoMinutos()));
				})
				.toList();

		if (linhas.isEmpty()) {
			return "Nenhum lancamento de banco de horas encontrado para os filtros informados.";
		}

		String cabecalho = periodoResolvido == null
				? "Lancamentos de banco de horas:"
				: "Lancamentos de banco de horas para " + periodoResolvido.rotulo() + ":";

		return cabecalho + "\n" + String.join("\n", linhas);
	}

	private Map<String, FuncionarioRegistro> funcionariosPorMatricula() {
		return portaFuncionarios.listarFuncionarios().stream()
				.collect(Collectors.toMap(FuncionarioRegistro::matricula, funcionario -> funcionario));
	}

	private MesResolvido resolverMes(String valor) {
		if (valor == null || valor.isBlank()) {
			throw new IllegalArgumentException("Mes obrigatorio. Use formatos como abril, 2026-04 ou 04/2026.");
		}

		String normalizado = normalizar(valor);
		Matcher anoMes = ANO_MES.matcher(normalizado);
		if (anoMes.matches()) {
			int ano = Integer.parseInt(anoMes.group(1));
			int mes = Integer.parseInt(anoMes.group(2));
			return new MesResolvido(YearMonth.of(ano, mes), nomeMes(mes) + " de " + ano);
		}

		Matcher mesAno = MES_ANO.matcher(normalizado);
		if (mesAno.matches()) {
			int mes = Integer.parseInt(mesAno.group(1));
			int ano = Integer.parseInt(mesAno.group(2));
			return new MesResolvido(YearMonth.of(ano, mes), nomeMes(mes) + " de " + ano);
		}

		Integer numeroMes = MESES.get(normalizado);
		if (numeroMes != null) {
			return new MesResolvido(
					YearMonth.of(anoPadrao, numeroMes),
					nomeMes(numeroMes) + " de " + anoPadrao + " (ano assumido pelo servidor)");
		}

		throw new IllegalArgumentException("Mes invalido. Use formatos como abril, 2026-04 ou 04/2026.");
	}

	private PeriodoResolvido resolverPeriodo(String periodo) {
		if (periodo == null || periodo.isBlank()) {
			return null;
		}

		String normalizado = normalizar(periodo);
		if (normalizado.matches("20\\d{2}")) {
			return new PeriodoResolvido(normalizado, normalizado, TipoPeriodo.ANO);
		}

		Matcher anoMes = ANO_MES.matcher(normalizado);
		if (anoMes.matches()) {
			int ano = Integer.parseInt(anoMes.group(1));
			int mes = Integer.parseInt(anoMes.group(2));
			return new PeriodoResolvido(YearMonth.of(ano, mes).toString(), nomeMes(mes) + " de " + ano, TipoPeriodo.MES);
		}

		Matcher mesAno = MES_ANO.matcher(normalizado);
		if (mesAno.matches()) {
			int mes = Integer.parseInt(mesAno.group(1));
			int ano = Integer.parseInt(mesAno.group(2));
			return new PeriodoResolvido(YearMonth.of(ano, mes).toString(), nomeMes(mes) + " de " + ano, TipoPeriodo.MES);
		}

		if (MESES.containsKey(normalizado)) {
			MesResolvido mesResolvido = resolverMes(normalizado);
			return new PeriodoResolvido(mesResolvido.valor().toString(), mesResolvido.rotulo(), TipoPeriodo.MES);
		}

		throw new IllegalArgumentException("Periodo invalido. Use ano como 2026 ou mes como abril, 2026-04 ou 04/2026.");
	}

	private boolean correspondeAoPeriodo(String mesReferencia, PeriodoResolvido periodoResolvido) {
		if (periodoResolvido == null) {
			return true;
		}
		return switch (periodoResolvido.tipo()) {
			case ANO -> mesReferencia.startsWith(periodoResolvido.valor() + "-");
			case MES -> mesReferencia.equals(periodoResolvido.valor());
		};
	}

	private String formatarFuncionarios(List<FuncionarioRegistro> funcionarios, String cabecalho) {
		return funcionarios.stream()
				.sorted(Comparator.comparing(FuncionarioRegistro::nome))
				.map(funcionario -> "%s | %s | admissao %s | aniversario %s".formatted(
						funcionario.matricula(),
						funcionario.nome(),
						formatarData(funcionario.dataAdmissao()),
						formatarData(funcionario.aniversario())))
				.collect(Collectors.joining("\n", cabecalho + "\n", ""));
	}

	private String formatarData(java.time.LocalDate data) {
		return data.format(FORMATO_DATA);
	}

	private String formatarMinutos(int totalMinutos) {
		String sinal = totalMinutos < 0 ? "-" : "";
		int absoluto = Math.abs(totalMinutos);
		int horas = absoluto / 60;
		int minutos = absoluto % 60;
		return "%s%dh%02d".formatted(sinal, horas, minutos);
	}

	private String nomeMes(int mes) {
		return switch (mes) {
			case 1 -> "janeiro";
			case 2 -> "fevereiro";
			case 3 -> "marco";
			case 4 -> "abril";
			case 5 -> "maio";
			case 6 -> "junho";
			case 7 -> "julho";
			case 8 -> "agosto";
			case 9 -> "setembro";
			case 10 -> "outubro";
			case 11 -> "novembro";
			case 12 -> "dezembro";
			default -> throw new IllegalArgumentException("Mes invalido: " + mes);
		};
	}

	private String normalizar(String valor) {
		return valor == null ? "" : valor.toLowerCase(Locale.ROOT)
				.replace("ç", "c")
				.replace("ã", "a")
				.replace("á", "a")
				.replace("à", "a")
				.replace("â", "a")
				.replace("é", "e")
				.replace("ê", "e")
				.replace("í", "i")
				.replace("ó", "o")
				.replace("ô", "o")
				.replace("õ", "o")
				.replace("ú", "u")
				.strip();
	}

	private record MesResolvido(YearMonth valor, String rotulo) {
	}

	private record PeriodoResolvido(String valor, String rotulo, TipoPeriodo tipo) {
	}

	private enum TipoPeriodo {
		ANO,
		MES
	}

}
