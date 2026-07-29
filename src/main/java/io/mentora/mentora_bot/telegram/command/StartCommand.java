package io.mentora.mentora_bot.telegram.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.mentora.mentora_bot.service.TelegramUserService;
import io.mentora.mentora_bot.telegram.model.RegistrationResult;
import io.mentora.mentora_bot.telegram.model.RegistrationStatus;
import io.mentora.mentora_bot.telegram.model.TelegramContext;
import io.mentora.mentora_bot.telegram.service.TelegramMessageService;
import lombok.RequiredArgsConstructor;

@Component
public class StartCommand implements TelegramCommand {

	private final TelegramMessageService messageService;
	private final TelegramUserService telegramUserService;

	public StartCommand(TelegramMessageService messageService, TelegramUserService telegramUserService) {
		super();
		this.messageService = messageService;
		this.telegramUserService = telegramUserService;
	}

	@Override
	public String getCommand() {
		return "/start";
	}

	@Override
	public SendMessage execute(TelegramContext context) {

		RegistrationResult result = telegramUserService.register(context);
		if (result.getStatus() == RegistrationStatus.NEW_USER) {
			return messageService.createMessage(result.getUser().getChatId(),
					"Hello " + result.getUser().getFirstName() + "! I am Mentora Bot 🤖");
		} else {
			return messageService.createMessage(result.getUser().getChatId(),
					"Hello " + result.getUser().getFirstName() + "! Welcome back!");
		}
	}

}