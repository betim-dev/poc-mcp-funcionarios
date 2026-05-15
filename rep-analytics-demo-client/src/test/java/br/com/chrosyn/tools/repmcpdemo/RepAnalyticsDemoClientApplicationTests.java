package br.com.chrosyn.tools.repmcpdemo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(properties = {
		"demo.terminal.enabled=false",
		"spring.ai.mcp.client.enabled=false"
})
class RepAnalyticsDemoClientApplicationTests {

	@Test
	void contextLoads(ApplicationContext applicationContext) {
		assertThat(applicationContext.getBean(ChatClient.Builder.class)).isNotNull();
		assertThat(applicationContext.containsBean("terminalChatRunner")).isFalse();
	}

}
