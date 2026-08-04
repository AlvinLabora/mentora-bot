package io.mentora.mentora_bot.telegram.ai.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import io.mentora.mentora_bot.config.OpenRouterServiceProps;
import io.mentora.mentora_bot.telegram.ai.providers.openrouter.client.OpenRouterClient;
import io.mentora.mentora_bot.telegram.ai.providers.openrouter.dto.request.OpenRouterRequest;
import io.mentora.mentora_bot.telegram.ai.providers.openrouter.dto.request.RequestMessage;
import io.mentora.mentora_bot.telegram.ai.providers.openrouter.dto.response.OpenRouterResponse;
import io.mentora.mentora_bot.telegram.model.TelegramContext;

@Service
public class AIService {

	private final OpenRouterClient openRouterClient;
	private final OpenRouterServiceProps props;
	private final Logger log = LoggerFactory.getLogger(AIService.class);

	public AIService(OpenRouterClient openRouterClient, OpenRouterServiceProps props) {
		this.openRouterClient = openRouterClient;
		this.props = props;
	}

	public String ask(TelegramContext context) {

		OpenRouterRequest request = new OpenRouterRequest();

		RequestMessage UserRequestMessage = new RequestMessage();
		UserRequestMessage.setRole("user");
		UserRequestMessage.setContent(context.getText());

		RequestMessage SystemRequestMessage = new RequestMessage();
		SystemRequestMessage.setRole("system");
		SystemRequestMessage.setContent(getSystemPrompt());

		List<RequestMessage> requestMessages = new ArrayList<>();
		requestMessages.add(SystemRequestMessage);
		requestMessages.add(UserRequestMessage);

		request.setMessages(requestMessages);

		for (String model : props.getModels()) {

			request.setModel(model);

			try {

				OpenRouterResponse response = openRouterClient.chat(request);

				return response.getChoices().get(0).getMessage().getContent();

			} catch (HttpClientErrorException.TooManyRequests e) {
				log.warn("Model [{}] is rate limited. Trying next model...", model);
			} catch (HttpClientErrorException.Unauthorized e) {
				log.warn("{} unauthorized, trying next model.", model);

			}

		}

		return "Sorry, all AI services are currently busy. Please try again in a few moments.";
	}

	private String getSystemPrompt() {
		return String.join("\n", "You are Mentora, a personal AI mentor on Telegram.",
				"You were created by Alvin Labora as a personal learning assistant.",
				"You are powered by an AI language model, but users interact with you as Mentora.",
				"",
				"Rules:",
				"- Give direct answers without unnecessary introductions.",
				"- Keep responses concise unless the user explicitly asks for a detailed explanation.",
				"- Do not use Markdown formatting such as *, #, ##, **, or bullet symbols.",
				"- Write in plain text suitable for Telegram messages.",
				"- If the question is simple, answer in 1-3 short paragraphs.",
				"- If the user asks for code, provide only the relevant code with a brief explanation.",
				"- If you don't know the answer, say so instead of making up information.",
				"- If users ask who created you, answer that you were created by Alvin Labora.",
				"- If users ask what AI technology powers you, explain that you are powered by an AI language model through OpenRouter.",
				"- Don't use markdown"

		);
	}
}
