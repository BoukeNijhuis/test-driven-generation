package nl.boukenijhuis.assistants.anthropic.dto;

import java.util.List;

public record AnthropicRequest(
        String model,
        List<Message> messages,
        int max_tokens
) {

    public record Message(
            String role,
            String content
    ) {
    }
}