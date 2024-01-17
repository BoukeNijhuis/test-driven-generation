package nl.boukenijhuis.assistants.llama2.dto;

public record Llama2Request(
        String model,
        String prompt,
        boolean stream) {

    public Llama2Request(String prompt) {
        this("llama2", prompt, false);
    }
}

