package nl.boukenijhuis.assistants.chatgpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.boukenijhuis.assistants.AbstractAIAssistant;
import nl.boukenijhuis.assistants.chatgpt.dto.ChatGptRequest;
import nl.boukenijhuis.assistants.chatgpt.dto.ChatGptResponse;
import nl.boukenijhuis.dto.PropertiesContainer;

import java.net.http.HttpResponse;
import java.util.List;

public class ChatGpt extends AbstractAIAssistant {

    protected String context = "";

    public ChatGpt(PropertiesContainer properties) {
        super(properties);
    }

    @Override
    protected String getFamily() {
        return "chatgpt";
    }

    @Override
    protected String getContent(HttpResponse<String> response) throws JsonProcessingException {
        var responseClass = objectMapper.readValue(response.body(), ChatGptResponse.class);
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
        var messageList = List.of(new ChatGptRequest.Message("user", updatedPrompt));
        int maxTokens = properties.getMaxTokens();
        var chatGptRequest = new ChatGptRequest(properties.getModel(), messageList, maxTokens);
        return objectMapper.writeValueAsString(chatGptRequest);
    }
}