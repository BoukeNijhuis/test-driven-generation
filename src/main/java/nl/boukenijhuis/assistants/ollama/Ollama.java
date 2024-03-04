package nl.boukenijhuis.assistants.ollama;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.boukenijhuis.assistants.AbstractAIAssistant;
import nl.boukenijhuis.assistants.ollama.dto.OllamaRequest;
import nl.boukenijhuis.assistants.ollama.dto.OllamaResponse;

import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class Ollama extends AbstractAIAssistant {

    protected List<Integer> context = Collections.emptyList();
    protected String model;

    public Ollama(Properties properties) {
        super(properties);
        this.model = properties.getProperty("ollama.model");
    }

    @Override
    protected String getPropertyPrefix() {
        return "ollama";
    }

    @Override
    protected String getContent(HttpResponse<String> response) throws JsonProcessingException {
        var responseClass = objectMapper.readValue(response.body(), OllamaResponse.class);
        this.context = responseClass.context();
        return responseClass.response();
    }

    @Override
    protected String createRequestBody(String prompt) throws JsonProcessingException {
        OllamaRequest request = new OllamaRequest(model, prompt, this.context);
        return objectMapper.writeValueAsString(request);
    }

    public List<Integer> getContext() {
        return context;
    }
}
