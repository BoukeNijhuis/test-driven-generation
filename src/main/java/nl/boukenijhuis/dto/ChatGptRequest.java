package nl.boukenijhuis.dto;

import java.util.List;

public record ChatGptRequest(
        String model,
        List<MessageDTO> messages,
        int max_tokens
) {

    public record MessageDTO(
            String role,
            String content
    ) {
    }
}