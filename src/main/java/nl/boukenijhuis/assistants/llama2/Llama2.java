package nl.boukenijhuis.assistants.llama2;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.boukenijhuis.assistants.AbstractAIAssistant;
import nl.boukenijhuis.assistants.llama2.dto.Llama2Request;
import nl.boukenijhuis.assistants.llama2.dto.Llama2Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class Llama2 extends AbstractAIAssistant {

    private static final Logger LOG = LogManager.getLogger();

    private List<Integer> context = Collections.emptyList();

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
        this.context = responseClass.context();
        return responseClass.response();
    }

    @Override
    protected String createRequestBody(String prompt) throws JsonProcessingException {
        Llama2Request request = new Llama2Request(prompt, this.context);
        return objectMapper.writeValueAsString(request);
    }

    public List<Integer> getContext() {
        return context;
    }
}
