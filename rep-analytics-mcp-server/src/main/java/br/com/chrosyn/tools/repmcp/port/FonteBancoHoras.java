package br.com.chrosyn.tools.repmcp.port;

import java.util.List;

import br.com.chrosyn.tools.repmcp.domain.BancoHorasRegistro;

public interface FonteBancoHoras {

	List<BancoHorasRegistro> listarLancamentos();

}
