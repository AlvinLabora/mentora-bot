package io.mentora.mentora_bot.telegram.ai.providers.openrouter.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpenRouterResponse {

    private List<ResponseChoice> choices;

}
