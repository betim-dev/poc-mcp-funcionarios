package br.com.chrosyn.tools.repmcp;

import java.time.Clock;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RepAnalyticsMcpServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RepAnalyticsMcpServerApplication.class, args);
	}

	@Bean
	Clock clock() {
		return Clock.systemDefaultZone();
	}

	@Bean
	ToolCallbackProvider toolProvider(RepAnalyticsTools repAnalyticsTools) {
		return MethodToolCallbackProvider.builder().toolObjects(repAnalyticsTools).build();
	}

}
