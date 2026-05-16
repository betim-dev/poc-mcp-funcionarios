package br.com.chrosyn.tools.repmcp.adapter.simulado;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import br.com.chrosyn.tools.repmcp.adapter.support.JsonClasspathLoader;
import br.com.chrosyn.tools.repmcp.domain.BancoHorasRegistro;
import br.com.chrosyn.tools.repmcp.port.FonteBancoHoras;

@Service
public class AdaptadorSimuladoBancoHoras implements FonteBancoHoras {

	private final List<BancoHorasRegistro> lancamentos;

	public AdaptadorSimuladoBancoHoras(
			JsonClasspathLoader loader,
			@Value("${rep.analytics.hour-bank-data}") String hourBankDataLocation) {
		this.lancamentos = List.copyOf(loader.loadList(hourBankDataLocation, new TypeReference<List<BancoHorasRegistro>>() {
		}));
	}

	@Override
	public List<BancoHorasRegistro> listarLancamentos() {
		return lancamentos;
	}

}
