package br.com.chrosyn.tools.repmcp;

import java.time.LocalDate;

public record FuncionarioRegistro(
		String matricula,
		String nome,
		LocalDate dataAdmissao,
		LocalDate aniversario) {
}
