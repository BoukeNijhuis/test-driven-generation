package nl.boukenijhuis.assistants.llama2;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.boukenijhuis.assistants.AbstractAIAssistant;
import nl.boukenijhuis.assistants.llama2.dto.Llama2Request;
import nl.boukenijhuis.assistants.llama2.dto.Llama2Response;

import java.net.http.HttpResponse;
import java.util.Properties;

public class Llama2 extends AbstractAIAssistant {

    public Llama2(Properties properties) {
        super(properties);
    }

    @Override
    protected String getPropertyPrefix() {
        return "llama2";
    }

    @Override
    protected String getContent(HttpResponse<String> response) throws JsonProcessingException {
        var responseClass = objectMapper.readValue(response.body(), Llama2Response.class);
        return responseClass.response();
    }

    @Override
    protected String createRequestBody(String prompt) throws JsonProcessingException {
        Llama2Request request = new Llama2Request(prompt);
        return objectMapper.writeValueAsString(request);
    }

}
