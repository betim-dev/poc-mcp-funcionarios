package br.com.chrosyn.tools.repmcp.domain;

import java.time.LocalDate;

public record FuncionarioRegistro(
		String matricula,
		String nome,
		LocalDate dataAdmissao,
		LocalDate aniversario) {
}
