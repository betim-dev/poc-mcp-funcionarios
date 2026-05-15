package br.com.chrosyn.tools.repmcp;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class AdaptadorSimuladoFuncionarios implements PortaFuncionarios {

	private final List<FuncionarioRegistro> funcionarios;

	public AdaptadorSimuladoFuncionarios(
			JsonClasspathLoader loader,
			@Value("${rep.analytics.employee-data}") String employeeDataLocation) {
		this.funcionarios = List.copyOf(loader.loadList(employeeDataLocation, new TypeReference<List<FuncionarioRegistro>>() {
		}));
	}

	@Override
	public List<FuncionarioRegistro> listarFuncionarios() {
		return funcionarios;
	}

}
