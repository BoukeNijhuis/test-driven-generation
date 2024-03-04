package nl.boukenijhuis.assistants.ollama.dto;

import java.util.List;

public record OllamaRequest(
        String model,
        String prompt,
        List<Integer> context,
        boolean stream) {

    public OllamaRequest(String model, String prompt, List<Integer> context) {
        this(model, prompt, context, false);
    }
}

