package io.mentora.mentora_bot.telegram.ai.providers.openrouter.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpenRouterRequest {

    private String model;
    private List<RequestMessage> messages;

}
