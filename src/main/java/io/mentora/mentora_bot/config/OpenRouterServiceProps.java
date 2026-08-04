package io.mentora.mentora_bot.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "openrouter.service.props")
@Qualifier("openRouterServiceProps")
public class OpenRouterServiceProps {

	private String apiUrl;
	private String apiKey;
	private List<String> models;
}
