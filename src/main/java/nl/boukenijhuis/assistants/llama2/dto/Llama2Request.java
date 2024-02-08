package nl.boukenijhuis.assistants.llama2.dto;

import java.util.List;

public record Llama2Request(
        String model,
        String prompt,
        List<Integer> context,
        boolean stream) {

    public Llama2Request(String prompt) {
        this("codellama", prompt, null, false);
    }

    public Llama2Request(String prompt, List<Integer> context) {
        this("codellama", prompt, context, false);
    }
}

