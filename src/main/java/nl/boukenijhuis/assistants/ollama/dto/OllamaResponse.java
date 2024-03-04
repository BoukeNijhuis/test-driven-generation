package nl.boukenijhuis.assistants.ollama.dto;

import java.util.Date;
import java.util.List;

public record OllamaResponse(
        String model,
        Date created_at,
        String response,
        boolean done,
        List<Integer> context,
        long total_duration,
        long load_duration,
        int prompt_eval_count,
        long prompt_eval_duration,
        int eval_count,
        long eval_duration) {
}

