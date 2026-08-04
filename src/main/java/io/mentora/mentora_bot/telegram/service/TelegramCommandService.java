package io.mentora.mentora_bot.telegram.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.mentora.mentora_bot.telegram.command.TelegramCommand;
import io.mentora.mentora_bot.telegram.command.UnknownCommand;
import io.mentora.mentora_bot.telegram.conversation.ConversationService;
import io.mentora.mentora_bot.telegram.model.TelegramContext;

@Service
public class TelegramCommandService {

	private final Map<String, TelegramCommand> commandMap = new HashMap<>();
	private final UnknownCommand unknownCommand;
	private final ConversationService conversationService;

	public TelegramCommandService(List<TelegramCommand> commands, UnknownCommand unknownCommand,
			ConversationService conversationService) {
		this.unknownCommand = unknownCommand;
		this.conversationService = conversationService;

		for (TelegramCommand command : commands) {
			commandMap.put(command.getCommand(), command);
		}
	}

	public SendMessage handle(Update update) {

		if (!update.hasMessage() || !update.getMessage().hasText()) {
			return null;
		}

		TelegramContext context = new TelegramContext();
		context.setChatId(update.getMessage().getChatId().toString());
		context.setUserId(update.getMessage().getFrom().getId());
		context.setUsername(update.getMessage().getFrom().getUserName());
		context.setFirstName(update.getMessage().getFrom().getFirstName());
		context.setText(update.getMessage().getText());

		TelegramCommand command = commandMap.get(context.getText().toLowerCase());

		if (command != null) {
			return command.execute(context);
		}

		if (context.getText().startsWith("/")) {
			return unknownCommand.execute(context);
		}

		return conversationService.chat(context);

	}
}