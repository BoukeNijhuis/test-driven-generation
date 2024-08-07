package nl.boukenijhuis.assistants.anthropic;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.boukenijhuis.assistants.AbstractAIAssistant;
import nl.boukenijhuis.assistants.anthropic.dto.AnthropicRequest;
import nl.boukenijhuis.assistants.anthropic.dto.AnthropicResponse;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Properties;

public class Anthropic extends AbstractAIAssistant {

    protected String context = "";

    public Anthropic(Properties properties) {
        super(properties);
    }

    @Override
    protected String getPropertyPrefix() {
        return "anthropic";
    }

    @Override
    protected String getContent(HttpResponse<String> response) throws JsonProcessingException {
        var responseClass = objectMapper.readValue(response.body(), AnthropicResponse.class);
        String content = responseClass.content().get(0).text();
        // use the entire content as context
        this.context = content;
        return content;
    }

    @Override
    protected String createRequestBody(String prompt) throws JsonProcessingException {
        // put context in front of the provided prompt
        String updatedPrompt = String.format("Previous answer: %s\n\n%s", context, prompt);
        var messageList = List.of(new AnthropicRequest.Message("user", updatedPrompt));
        int maxTokens = Integer.parseInt((String) properties.get(getPropertyPrefix() + ".maxTokens"));
        var chatGptRequest = new AnthropicRequest(properties.getProperty(getPropertyPrefix() + ".model"), messageList, maxTokens);
        return objectMapper.writeValueAsString(chatGptRequest);
    }
}