package br.com.chrosyn.tools.repmcpdemo;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class RepAnalyticsDemoClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(RepAnalyticsDemoClientApplication.class, args);
	}

	@Bean
	@ConditionalOnBean(ToolCallbackProvider.class)
	@ConditionalOnProperty(name = "spring.ai.mcp.client.enabled", havingValue = "true", matchIfMissing = true)
	ToolCallbackProvider provedorAuditoriaTools(ToolCallbackProvider delegate) {
		return () -> Arrays.stream(delegate.getToolCallbacks())
				.map(ToolComAuditoria::new)
				.toArray(ToolCallback[]::new);
	}

}

final class ToolComAuditoria implements ToolCallback {

	private final ToolCallback delegate;

	ToolComAuditoria(ToolCallback delegate) {
		this.delegate = delegate;
	}

	@Override
	public ToolDefinition getToolDefinition() {
		return delegate.getToolDefinition();
	}

	@Override
	public String call(String toolInput) {
		System.out.println("[AUDITORIA] tool.inicio nome=" + getToolDefinition().name() + " entrada=" + toolInput);
		String result = delegate.call(toolInput);
		System.out.println("[AUDITORIA] tool.fim nome=" + getToolDefinition().name() + " saida=" + resumir(result));
		return result;
	}

	private String resumir(String value) {
		if (value == null) {
			return "null";
		}
		String singleLine = value.replace("\r", " ").replace("\n", " ").strip();
		return singleLine.length() <= 240 ? singleLine : singleLine.substring(0, 240) + "...";
	}

}

@Component
@ConditionalOnProperty(name = "demo.terminal.enabled", havingValue = "true", matchIfMissing = true)
class TerminalChatRunner implements CommandLineRunner {

	private final ChatClient chatClient;
	private final ToolCallbackProvider provedorAuditoriaTools;

	TerminalChatRunner(ChatClient.Builder chatClientBuilder, ToolCallbackProvider provedorAuditoriaTools) {
		this.chatClient = chatClientBuilder
				.defaultSystem("""
						Voce e um assistente de RH e ponto eletronico.
						Responda sempre em portugues do Brasil.
						Interprete a pergunta do usuario com base nas tools MCP disponiveis.
						Quando a pergunta depender dos dados de funcionarios ou banco de horas, use as tools MCP disponiveis.
						Nao invente respostas com base em memoria da conversa se existir uma tool apropriada.
						Se nenhuma tool suportar a consulta, diga explicitamente que a capability nao existe no MCP atual.
						Use as descricoes das tools para decidir qual chamar.
						Nao assuma perguntas predefinidas; derive a resposta a partir dos dados retornados pelas tools.
						""")
				.defaultToolCallbacks(provedorAuditoriaTools)
				.build();
		this.provedorAuditoriaTools = provedorAuditoriaTools;
	}

	@Override
	public void run(String... args) {
		List<String> history = new ArrayList<>();
		printWelcome();

		try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
			while (true) {
				System.out.print("voce> ");
				if (!scanner.hasNextLine()) {
					break;
				}

				String input = scanner.nextLine().strip();
				if (input.isBlank()) {
					continue;
				}
				if (isExit(input)) {
					System.out.println("assistente> Encerrando.");
					return;
				}
				if (input.equalsIgnoreCase("ajuda")) {
					printHelp();
					continue;
				}
				if (input.equalsIgnoreCase("tools")) {
					printTools();
					continue;
				}

				String prompt = buildPrompt(history, input);
				try {
					String answer = chatClient.prompt()
							.user(prompt)
							.call()
							.content();

					history.add("Usuario: " + input);
					history.add("Assistente: " + answer);
					System.out.println("assistente> " + answer);
				}
				catch (Exception exception) {
					System.out.println("assistente> Falha ao consultar o modelo ou o MCP: " + exception.getMessage());
				}
			}
		}
	}

	private String buildPrompt(List<String> history, String input) {
		if (history.isEmpty()) {
			return input;
		}
		StringBuilder builder = new StringBuilder("Historico recente:\n");
		int start = Math.max(0, history.size() - 6);
		for (int index = start; index < history.size(); index++) {
			builder.append(history.get(index)).append("\n");
		}
		builder.append("\nPergunta atual:\n").append(input);
		return builder.toString();
	}

	private boolean isExit(String input) {
		return input.equalsIgnoreCase("sair")
				|| input.equalsIgnoreCase("exit")
				|| input.equalsIgnoreCase("quit");
	}

	private void printWelcome() {
		System.out.println("Chat MCP de funcionarios e banco de horas");
		System.out.println("Digite sua pergunta, 'tools' para listar as tools MCP, 'ajuda' para exemplos ou 'sair' para encerrar.");
		System.out.println("Auditoria de tools habilitada: cada chamada MCP sera impressa com [AUDITORIA].");
	}

	private void printHelp() {
		System.out.println("Exemplos:");
		System.out.println("- Qual funcionario no mes de abril tem mais banco de horas?");
		System.out.println("- Qual funcionario tem mais banco de horas em 2026?");
		System.out.println("- Quantos funcionarios foram contratados em 2026?");
		System.out.println("- Quais funcionarios fazem aniversario em maio?");
		System.out.println("- Qual funcionario tem mais tempo de casa?");
		System.out.println("- Liste os funcionarios simulados.");
		System.out.println("- Mostre o banco de horas de abril.");
	}

	private void printTools() {
		System.out.println("Tools MCP carregadas:");
		for (ToolCallback toolCallback : provedorAuditoriaTools.getToolCallbacks()) {
			System.out.println("- " + toolCallback.getToolDefinition().name());
		}
	}

}
