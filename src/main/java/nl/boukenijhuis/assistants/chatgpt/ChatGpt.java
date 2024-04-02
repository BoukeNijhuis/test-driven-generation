package nl.boukenijhuis.assistants.chatgpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.boukenijhuis.assistants.AbstractAIAssistant;
import nl.boukenijhuis.assistants.chatgpt.dto.ChatGptRequest;
import nl.boukenijhuis.assistants.chatgpt.dto.ChatGptResponse;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Properties;

public class ChatGpt extends AbstractAIAssistant {

    protected String context = "";

    public ChatGpt(Properties properties) {
        super(properties);
    }

    @Override
    protected String getPropertyPrefix() {
        return "chatgpt";
    }

    @Override
    protected String getContent(HttpResponse<String> response) throws JsonProcessingException {
        var responseClass = objectMapper.readValue(response.body(), ChatGptResponse.class);
        String content = responseClass.choices().get(0).message().content();
        // use the entire context as content
        this.context = content;
        return content;
    }

    @Override
    protected String createRequestBody(String prompt) throws JsonProcessingException {
        // put context in front of the provided prompt
        String updatedPrompt = String.format("Previous answer: %s\n\n%s", context, prompt);
        var messageList = List.of(new ChatGptRequest.MessageDTO("user", updatedPrompt));
        int maxTokens = Integer.parseInt((String) properties.get("chatgpt.maxTokens"));
        var chatGptRequest = new ChatGptRequest(properties.getProperty("chatgpt.model"), messageList, maxTokens);
        return objectMapper.writeValueAsString(chatGptRequest);
    }
}