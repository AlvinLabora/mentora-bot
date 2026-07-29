package io.mentora.mentora_bot.telegram.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.mentora.mentora_bot.telegram.model.TelegramContext;
import io.mentora.mentora_bot.telegram.service.TelegramMessageService;

@Component
public class UnknownCommand {

	@Autowired
	private TelegramMessageService messageService;

	public SendMessage execute(TelegramContext context) {
		String chatId = context.getChatId();
		return  messageService.createMessage(chatId, "❌ Unknown command. Type /help to see the available commands.");

	}

}
