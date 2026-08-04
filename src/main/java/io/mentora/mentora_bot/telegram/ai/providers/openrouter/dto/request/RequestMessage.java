package io.mentora.mentora_bot.telegram.ai.providers.openrouter.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RequestMessage {

    private String role;
    private String content;

}
