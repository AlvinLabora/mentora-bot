package io.mentora.mentora_bot.telegram.conversation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import io.mentora.mentora_bot.entity.Conversation;
import io.mentora.mentora_bot.repository.ConversationRepository;
import io.mentora.mentora_bot.telegram.ai.providers.openrouter.dto.request.RequestMessage;

@Component
public class ConversationHistoryService {

	private final ConversationRepository conversationRepository;

	public ConversationHistoryService(ConversationRepository conversationRepository) {
		this.conversationRepository = conversationRepository;
	}

	public List<RequestMessage> buildHistory(Long telegramUserId, String userMessage) {
		List<Conversation> conversations = conversationRepository.findByTelegramUserId(telegramUserId);
		List<RequestMessage> messages = new ArrayList<>();

		for (Conversation conversation : conversations) {
			RequestMessage request = new RequestMessage();
			request.setRole(conversation.getRole());
			request.setContent(conversation.getContent());
			messages.add(request);
		}

		RequestMessage currentUserMessage = new RequestMessage();
		currentUserMessage.setRole("user");
		currentUserMessage.setContent(userMessage);
		messages.add(currentUserMessage);

		return messages;
	}
}
