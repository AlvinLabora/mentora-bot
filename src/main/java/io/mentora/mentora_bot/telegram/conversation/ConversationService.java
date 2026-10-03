package io.mentora.mentora_bot.telegram.conversation;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

	private static final String AI_UNAVAILABLE_MESSAGE = "Sorry, I can't answer right now. Please try again in a few moments.";

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

		Optional<String> reply = aiService.ask(messages);

		// If the AI failed, tell the user but do NOT store the exchange,
		// otherwise error text / unanswered questions pollute the next prompt.
		if (!reply.isPresent()) {
			return messageService.createMessage(chatId, AI_UNAVAILABLE_MESSAGE);
		}

		saveConversation(userId, userMessage, reply.get());

		return messageService.createMessage(chatId, reply.get());
	}

	public void saveConversation(Long userId, String userMessage, String assistantMessage) {

		LocalDateTime now = LocalDateTime.now();

		Conversation userConversation = new Conversation();
		userConversation.setTelegramUserId(userId);
		userConversation.setRole("user");
		userConversation.setContent(userMessage);
		userConversation.setCreatedAt(now);

		Conversation assistantConversation = new Conversation();
		assistantConversation.setTelegramUserId(userId);
		assistantConversation.setRole("assistant");
		assistantConversation.setContent(assistantMessage);
		assistantConversation.setCreatedAt(now);

		// saveAll runs in one transaction: both rows are stored or neither.
		conversationRepository.saveAll(Arrays.asList(userConversation, assistantConversation));
	}

	/**
	 * Deletes all stored messages of this user (used by /reset).
	 *
	 * @return how many stored messages were deleted
	 */
	public int clearHistory(Long userId) {
		return conversationRepository.clearByTelegramUserId(userId);
	}
}