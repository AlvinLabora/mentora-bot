package io.mentora.mentora_bot.telegram.model;

import io.mentora.mentora_bot.entity.TelegramUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationResult {

	private final TelegramUser user;
	private final RegistrationStatus status;
	public RegistrationResult(TelegramUser user, RegistrationStatus status) {
		super();
		this.user = user;
		this.status = status;
	}
	
	

}
