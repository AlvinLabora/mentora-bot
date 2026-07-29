package io.mentora.mentora_bot.telegram.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelegramContext {

	private String chatId;

	private Long userId;

	private String username;

	private String firstName;

	private String text;
}
