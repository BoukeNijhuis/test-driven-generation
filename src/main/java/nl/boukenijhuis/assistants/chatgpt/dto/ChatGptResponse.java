package nl.boukenijhuis.assistants.chatgpt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ChatGptResponse(

        String id,
        String object,
        long created,
        String model,
        List<Choice> choices,
        Usage usage,
        String system_fingerprint,
        String service_tier
) {
    public record Choice(
            int index,
            Message message,
            String logprobs,
            String finish_reason) {
    }

    public record Message(
            String role,
            String content,
            String refusal) {
    }

    public record Usage(
            int prompt_tokens,
            int completion_tokens,
            int total_tokens,
            @JsonProperty("prompt_tokens_details")
            PromptTokensDetails promptTokensDetails,
            @JsonProperty("completion_tokens_details")
            CompletionTokensDetails completionTokensDetails) {
    }

    public record PromptTokensDetails(
            int audio_tokens,
            int cached_tokens
    ) {
    }

    public record CompletionTokensDetails(
            int audio_tokens,
            int reasoning_tokens,
            int accepted_prediction_tokens,
            int rejected_prediction_tokens
    ) {
    }
}
