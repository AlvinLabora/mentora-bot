package io.mentora.mentora_bot.telegram.ai.providers.openrouter.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mentora.mentora_bot.config.OpenRouterServiceProps;
import io.mentora.mentora_bot.telegram.ai.providers.openrouter.dto.request.OpenRouterRequest;
import io.mentora.mentora_bot.telegram.ai.providers.openrouter.dto.response.OpenRouterResponse;

@Component
public class OpenRouterClient {

	private final RestTemplate restTemplate;
	private final OpenRouterServiceProps props;
	private final Logger log = LoggerFactory.getLogger(OpenRouterClient.class);

	public OpenRouterClient(RestTemplate restTemplate, OpenRouterServiceProps props) {
		this.restTemplate = restTemplate;
		this.props = props;
	}

	public OpenRouterResponse chat(OpenRouterRequest request) {

		try {
			OpenRouterResponse response = restTemplate.exchange(props.getApiUrl(), HttpMethod.POST, new HttpEntity<>(request, getHeaders(props.getApiKey())), OpenRouterResponse.class).getBody();
			log.debug("OpenRouter Response: {}", response);
			return response;
		} catch (HttpClientErrorException e) {
			log.error("HTTP error from OpenRouter: {}", e.getStatusCode());
			throw e;
		} catch (RestClientException e) {
			log.error("Failed to connect to OpenRouter", e);
			throw e;
		}

	}

	protected HttpHeaders getHeaders(String apiKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Bearer " + apiKey);
		return headers;
	}

}
