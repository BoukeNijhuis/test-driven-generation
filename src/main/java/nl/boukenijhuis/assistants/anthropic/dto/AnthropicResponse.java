package nl.boukenijhuis.assistants.anthropic.dto;

import java.util.List;

public record AnthropicResponse(

        List<Content> content,
        String id,
        String model,
        String role,
        String stop_reason,
        String stop_sequence,
        String type,
        Usage usage
) {

    public record Content(
            String text,
            String type) {
    }

    public record Usage(
            int input_tokens,
            int output_tokens) {
    }
}