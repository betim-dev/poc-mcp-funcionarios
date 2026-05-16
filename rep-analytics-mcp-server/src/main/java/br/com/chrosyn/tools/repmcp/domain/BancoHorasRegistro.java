package br.com.chrosyn.tools.repmcp.domain;

public record BancoHorasRegistro(
		String matricula,
		String mesReferencia,
		int saldoMinutos) {
}
