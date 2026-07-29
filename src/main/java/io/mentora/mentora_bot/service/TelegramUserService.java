package io.mentora.mentora_bot.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.mentora.mentora_bot.entity.TelegramUser;
import io.mentora.mentora_bot.repository.TelegramUserRepository;
import io.mentora.mentora_bot.telegram.model.RegistrationResult;
import io.mentora.mentora_bot.telegram.model.RegistrationStatus;
import io.mentora.mentora_bot.telegram.model.TelegramContext;

@Service
public class TelegramUserService {

	private final TelegramUserRepository repository;

	public TelegramUserService(TelegramUserRepository repository) {
		super();
		this.repository = repository;
	}

	public RegistrationResult register(TelegramContext context) {

		LocalDateTime now = LocalDateTime.now();
		
		Optional<TelegramUser> existingUser = repository.findByTelegramUserId(context.getUserId());

		if (existingUser.isPresent()) {
			return new RegistrationResult(existingUser.get(), RegistrationStatus.EXISTING_USER);
		}

		TelegramUser registerUser = new TelegramUser();
		registerUser.setTelegramUserId(context.getUserId());
		registerUser.setChatId(context.getChatId());
		registerUser.setUsername(context.getUsername());
		registerUser.setFirstName(context.getFirstName());
		registerUser.setCreatedAt(now);
		registerUser.setUpdatedAt(now);
		TelegramUser user = repository.save(registerUser);
		return new RegistrationResult(user, RegistrationStatus.NEW_USER);

	}
}

