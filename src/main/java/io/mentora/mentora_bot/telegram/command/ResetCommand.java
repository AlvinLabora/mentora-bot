package io.mentora.mentora_bot.telegram.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.mentora.mentora_bot.telegram.conversation.ConversationService;
import io.mentora.mentora_bot.telegram.model.TelegramContext;
import io.mentora.mentora_bot.telegram.service.TelegramMessageService;

@Component
public class ResetCommand implements TelegramCommand {

	private final TelegramMessageService messageService;
	private final ConversationService conversationService;

	public ResetCommand(TelegramMessageService messageService, ConversationService conversationService) {
		this.messageService = messageService;
		this.conversationService = conversationService;
	}

	@Override
	public String getCommand() {
		return "/reset";
	}

	@Override
	public SendMessage execute(TelegramContext context) {

		int deleted = conversationService.clearHistory(context.getUserId());

		String message = deleted > 0
				? "🗑️ Done! I cleared our conversation. Let's start fresh."
				: "There's nothing to clear yet. We haven't talked about anything.";

		return messageService.createMessage(context.getChatId(), message);
	}

}