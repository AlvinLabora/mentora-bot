package io.mentora.mentora_bot.telegram.conversation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import io.mentora.mentora_bot.entity.Conversation;
import io.mentora.mentora_bot.repository.ConversationRepository;
import io.mentora.mentora_bot.telegram.ai.providers.openrouter.dto.request.RequestMessage;
import io.mentora.mentora_bot.telegram.ai.service.AIService;
import io.mentora.mentora_bot.telegram.model.TelegramContext;
import io.mentora.mentora_bot.telegram.service.TelegramMessageService;

@Component
public class ConversationService {

	private final TelegramMessageService messageService;
	private final AIService aiService;
	private final ConversationRepository conversationRepository;
	private final ConversationHistoryService conversationHistoryService;

	public ConversationService(TelegramMessageService messageService, AIService aiService,
			ConversationRepository conversationRepository, ConversationHistoryService conversationHistoryService) {
		this.messageService = messageService;
		this.aiService = aiService;
		this.conversationRepository = conversationRepository;
		this.conversationHistoryService = conversationHistoryService;
	}

	public SendMessage chat(TelegramContext context) {
		String chatId = context.getChatId();
		Long userId = context.getUserId();
		String userMessage = context.getText();

		List<RequestMessage> messages = conversationHistoryService.buildHistory(userId, userMessage);

		String response = aiService.ask(messages);

		saveConversation(userId, userMessage, response);

		return messageService.createMessage(chatId, response);
	}

	public void saveConversation(Long userId, String userMessage, String assistantMessage) {

		LocalDateTime now = LocalDateTime.now();
		Conversation userConversation = new Conversation();
		userConversation.setTelegramUserId(userId);
		userConversation.setRole("user");
		userConversation.setContent(userMessage);
		userConversation.setCreatedAt(now);

		conversationRepository.save(userConversation);

		Conversation assistantConversation = new Conversation();
		assistantConversation.setTelegramUserId(userId);
		assistantConversation.setRole("assistant");
		assistantConversation.setContent(assistantMessage);
		assistantConversation.setCreatedAt(now);

		conversationRepository.save(assistantConversation);
	}
}
