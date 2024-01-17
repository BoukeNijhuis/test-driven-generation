package nl.boukenijhuis.assistants.chatgpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.boukenijhuis.assistants.AbstractAIAssistant;
import nl.boukenijhuis.assistants.chatgpt.dto.ChatGptRequest;
import nl.boukenijhuis.assistants.chatgpt.dto.ChatGptResponse;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Properties;

public class ChatGpt extends AbstractAIAssistant {

    public ChatGpt(Properties properties) {
        super(properties);
    }

    @Override
    protected String getPropertyPrefix() {
        return "chatgpt";
    }

    @Override
    protected String getContent(HttpResponse<String> response) throws JsonProcessingException {
        ChatGptResponse chatGptResponse = objectMapper.readValue(response.body(), ChatGptResponse.class);
        return chatGptResponse.choices().get(0).message().content();
    }

    @Override
    protected String createRequestBody(String prompt) throws JsonProcessingException {
        List<ChatGptRequest.MessageDTO> messageList = List.of(new ChatGptRequest.MessageDTO("user", prompt));
        int maxTokens = Integer.parseInt((String) properties.get("chatgpt.maxTokens"));
        ChatGptRequest chatGptRequest = new ChatGptRequest("gpt-4", messageList, maxTokens);
        return objectMapper.writeValueAsString(chatGptRequest);
    }
}