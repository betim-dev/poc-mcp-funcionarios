package br.com.chrosyn.tools.repmcp.adapter.simulado;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import br.com.chrosyn.tools.repmcp.adapter.support.JsonClasspathLoader;
import br.com.chrosyn.tools.repmcp.domain.FuncionarioRegistro;
import br.com.chrosyn.tools.repmcp.port.FonteFuncionarios;

@Service
public class AdaptadorSimuladoFuncionarios implements FonteFuncionarios {

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
