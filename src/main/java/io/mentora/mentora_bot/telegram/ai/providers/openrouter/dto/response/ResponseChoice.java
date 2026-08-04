package io.mentora.mentora_bot.telegram.ai.providers.openrouter.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseChoice {

    private Integer index;
    private ResponseMessage message;

}
