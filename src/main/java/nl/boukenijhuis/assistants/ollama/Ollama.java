package nl.boukenijhuis.assistants.ollama;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.boukenijhuis.assistants.AbstractAIAssistant;
import nl.boukenijhuis.assistants.ollama.dto.OllamaRequest;
import nl.boukenijhuis.assistants.ollama.dto.OllamaResponse;
import nl.boukenijhuis.dto.PropertiesContainer;

import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class Ollama extends AbstractAIAssistant {

    protected List<Integer> context = Collections.emptyList();
    protected String model;

    public Ollama(PropertiesContainer properties) {
        super(properties);
        this.model = properties.getModel();
    }

    @Override
    protected String getFamily() {
        return "ollama";
    }

    @Override
    protected String getContent(HttpResponse<String> response) throws JsonProcessingException {
        var responseClass = objectMapper.readValue(response.body(), OllamaResponse.class);
        this.context = responseClass.context();
        return responseClass.response();
    }

    // Ollama does not need headers
    @Override
    protected String[] getHeaders() {
        return new String[0];
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
