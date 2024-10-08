package nl.boukenijhuis.assistants.chatgpt.dto;

import java.util.List;

public record ChatGptRequest(
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