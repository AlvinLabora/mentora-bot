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

	    String firstName = result.getUser().getFirstName();
	    String message;

	    if (result.getStatus() == RegistrationStatus.NEW_USER) {

	        message = "👋 Hello " + firstName + "! I'm Mentora, your personal AI mentor.\n\n"
	                + "I'm here to help you learn, practice, and understand things step by step.\n\n"
	                + "You can ask me anything. Let's start learning! 🚀";

	    } else {

	        message = "👋 Welcome back, " + firstName + "!\n\n"
	                + "Ready to continue learning? What are we working on today? 🚀";
	    }

	    return messageService.createMessage(
	            result.getUser().getChatId(),
	            message
	    );
	}

}