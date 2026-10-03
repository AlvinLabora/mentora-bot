package io.mentora.mentora_bot.telegram.conversation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import io.mentora.mentora_bot.entity.Conversation;
import io.mentora.mentora_bot.repository.ConversationRepository;
import io.mentora.mentora_bot.telegram.ai.providers.openrouter.dto.request.RequestMessage;

@Component
public class ConversationHistoryService {

	// Last N stored messages (user + assistant rows) sent as context.
	private static final int MAX_HISTORY_MESSAGES = 20;

	private final ConversationRepository conversationRepository;

	public ConversationHistoryService(ConversationRepository conversationRepository) {
		this.conversationRepository = conversationRepository;
	}

	public List<RequestMessage> buildHistory(Long telegramUserId, String userMessage) {

		// Newest first from the DB, so reverse into chronological order.
		List<Conversation> conversations = new ArrayList<>(conversationRepository
				.findByTelegramUserIdOrderByIdDesc(telegramUserId, PageRequest.of(0, MAX_HISTORY_MESSAGES)));
		Collections.reverse(conversations);

		List<RequestMessage> messages = new ArrayList<>();

		for (Conversation conversation : conversations) {
			messages.add(toRequestMessage(conversation.getRole(), conversation.getContent()));
		}

		messages.add(toRequestMessage("user", userMessage));

		return messages;
	}

	private RequestMessage toRequestMessage(String role, String content) {
		RequestMessage message = new RequestMessage();
		message.setRole(role);
		message.setContent(content);
		return message;
	}
}