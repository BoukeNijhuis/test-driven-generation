package nl.boukenijhuis.assistants.deepseek;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.boukenijhuis.assistants.AbstractAIAssistant;
import nl.boukenijhuis.assistants.deepseek.dto.DeepseekRequest;
import nl.boukenijhuis.assistants.deepseek.dto.DeepseekResponse;
import nl.boukenijhuis.dto.PropertiesContainer;

import java.net.http.HttpResponse;
import java.util.List;

public class Deepseek extends AbstractAIAssistant {

    protected String context = "";

    public Deepseek(PropertiesContainer properties) {
        super(properties);
    }

    @Override
    protected String getFamily() {
        return "deepseek";
    }

    @Override
    protected String getContent(HttpResponse<String> response) throws JsonProcessingException {
        var responseClass = objectMapper.readValue(response.body(), DeepseekResponse.class);
        String content = responseClass.choices().get(0).message().content();
        // use the entire content as context
        this.context = content;
        return content;
    }

    @Override
    protected String[] getHeaders() {
        return new String[] {"Authorization", "Bearer " + properties.getApiKey()};
    }

    @Override
    protected String createRequestBody(String prompt) throws JsonProcessingException {
        // put context in front of the provided prompt
        String updatedPrompt = String.format("Previous answer: %s\n\n%s", context, prompt);
        var messageList = List.of(new DeepseekRequest.Message("user", updatedPrompt));
        int maxTokens = properties.getMaxTokens();
        var chatGptRequest = new DeepseekRequest(properties.getModel(), messageList, maxTokens);
        return objectMapper.writeValueAsString(chatGptRequest);
    }
}