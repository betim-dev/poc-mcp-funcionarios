package br.com.chrosyn.tools.repmcp;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class AdaptadorSimuladoBancoHoras implements PortaBancoHoras {

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
