package io.mentora.mentora_bot.telegram.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.mentora.mentora_bot.telegram.model.TelegramContext;
import io.mentora.mentora_bot.telegram.service.TelegramMessageService;

@Component
public class HelpCommand implements TelegramCommand {

	@Autowired
	private TelegramMessageService messageService;

	@Override
	public String getCommand() {
		return "/help";
	}

	@Override
	public SendMessage execute(TelegramContext context) {
		String chatId = context.getChatId();
		return messageService.createMessage(chatId, "Available Commands:\n/start\n/help");
	}

}