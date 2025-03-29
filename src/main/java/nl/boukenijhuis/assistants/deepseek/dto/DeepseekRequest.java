package nl.boukenijhuis.assistants.deepseek.dto;

import java.util.List;

public record DeepseekRequest(
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