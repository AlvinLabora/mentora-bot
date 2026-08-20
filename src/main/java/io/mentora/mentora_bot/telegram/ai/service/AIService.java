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

@Service
public class AIService {

	private final OpenRouterClient openRouterClient;
	private final OpenRouterServiceProps props;
	private final Logger log = LoggerFactory.getLogger(AIService.class);

	public AIService(OpenRouterClient openRouterClient, OpenRouterServiceProps props) {
		this.openRouterClient = openRouterClient;
		this.props = props;
	}
	
	public String ask(List<RequestMessage> messages) {

		RequestMessage systemMessage = new RequestMessage();
	    systemMessage.setRole("system");
	    systemMessage.setContent(getSystemPrompt());

	    List<RequestMessage> requestMessages = new ArrayList<>();
	    requestMessages.add(systemMessage);
	    requestMessages.addAll(messages);

	    OpenRouterRequest request = new OpenRouterRequest();
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
	            "- If you don't know the answer, say so instead of making up information."
	    );
	}
}
