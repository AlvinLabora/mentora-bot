package io.mentora.mentora_bot.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "telegrambotconnection.service.props")
@Qualifier("telegramBotConnectionServiceProps")
public class TelegramBotConnectionServiceProps {

	private String telegramUsername;
	private String telemgramToken;
}
