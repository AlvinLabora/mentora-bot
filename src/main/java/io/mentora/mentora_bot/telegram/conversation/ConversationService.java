package io.mentora.mentora_bot.telegram.conversation;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.mentora.mentora_bot.telegram.ai.service.AIService;
import io.mentora.mentora_bot.telegram.model.TelegramContext;
import io.mentora.mentora_bot.telegram.service.TelegramMessageService;

@Component
public class ConversationService {

	private final TelegramMessageService messageService;
	private final AIService aiService;

	public ConversationService(TelegramMessageService messageService, AIService aiService) {
		this.messageService = messageService;
		this.aiService = aiService;
	}

	public SendMessage chat(TelegramContext context) {
		String chatId = context.getChatId();
		return messageService.createMessage(chatId, aiService.ask(context));

	}
}
