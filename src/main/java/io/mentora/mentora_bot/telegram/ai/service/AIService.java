package io.mentora.mentora_bot.telegram.ai.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import io.mentora.mentora_bot.config.OpenRouterServiceProps;
import io.mentora.mentora_bot.telegram.ai.providers.openrouter.client.OpenRouterClient;
import io.mentora.mentora_bot.telegram.ai.providers.openrouter.dto.request.OpenRouterRequest;
import io.mentora.mentora_bot.telegram.ai.providers.openrouter.dto.request.RequestMessage;
import io.mentora.mentora_bot.telegram.ai.providers.openrouter.dto.response.OpenRouterResponse;

@Service
public class AIService {

	private final OpenRouterClient openRouterClient;
	private final OpenRouterServiceProps props;
	private final Logger log = LoggerFactory.getLogger(AIService.class);

	public AIService(OpenRouterClient openRouterClient, OpenRouterServiceProps props) {
		this.openRouterClient = openRouterClient;
		this.props = props;
	}

	/**
	 * Tries each configured model in order until one returns a usable reply.
	 *
	 * @return the reply, or empty if every model failed (callers decide what to
	 *         tell the user and should not store the exchange).
	 */
	public Optional<String> ask(List<RequestMessage> messages) {

		List<String> models = props.getModels();
		if (models == null || models.isEmpty()) {
			log.error("No OpenRouter models configured");
			return Optional.empty();
		}

		RequestMessage systemMessage = new RequestMessage();
		systemMessage.setRole("system");
		systemMessage.setContent(getSystemPrompt());

		List<RequestMessage> requestMessages = new ArrayList<>();
		requestMessages.add(systemMessage);
		requestMessages.addAll(messages);

		OpenRouterRequest request = new OpenRouterRequest();
		request.setMessages(requestMessages);

		for (String model : models) {

			// Unset OPENROUTER_MODEL_n env vars resolve to blank strings.
			if (model == null || model.trim().isEmpty()) {
				continue;
			}

			request.setModel(model.trim());

			try {
				String content = extractContent(openRouterClient.chat(request));

				if (content != null) {
					return Optional.of(content);
				}
				log.warn("Model [{}] returned an empty response. Trying next model...", model);

			} catch (HttpClientErrorException.TooManyRequests e) {
				log.warn("Model [{}] is rate limited. Trying next model...", model);

			} catch (HttpClientErrorException.Unauthorized e) {
				// Usually a bad API key: a config problem, so make it loud.
				log.error("Model [{}] returned 401 Unauthorized. Check OPENROUTER_API_KEY.", model);

			} catch (RestClientException e) {
				// Covers 4xx/5xx (model removed, provider down, 402...) and timeouts.
				log.warn("Model [{}] failed: {}. Trying next model...", model, e.getMessage());
			}
		}

		log.error("All configured models failed");
		return Optional.empty();
	}

	private String extractContent(OpenRouterResponse response) {
		if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
			return null;
		}
		if (response.getChoices().get(0).getMessage() == null) {
			return null;
		}
		String content = response.getChoices().get(0).getMessage().getContent();
		return (content == null || content.trim().isEmpty()) ? null : content;
	}

	private String getSystemPrompt() {
		return String.join("\n",
				"You are Mentora, a personal AI mentor on Telegram.",
				"You were created by Alvin Labora as a personal learning assistant.",
				"You are powered by an AI language model, but users interact with you as Mentora.",
				"",
				"Response Rules:",
				"- Answer only what the user asked.",
				"- Keep every response short and focused.",
				"- Do not provide additional information unless the user asks for it.",
				"- Do not give a full tutorial unless the user explicitly asks for one.",
				"- For simple questions, answer in 1-3 short sentences.",
				"- For technical questions, explain only the specific concept asked about.",
				"- If code is requested, provide only the necessary code.",
				"- Ask a short follow-up question if more explanation is needed.",
				"- Never repeat the user's question.",
				"- Do not use Markdown.",
				"- Write plain text suitable for Telegram.",
				"- If you don't know the answer, say so instead of making up information.");
	}
}