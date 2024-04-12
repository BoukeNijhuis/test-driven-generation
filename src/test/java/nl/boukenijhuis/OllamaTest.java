package nl.boukenijhuis;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.boukenijhuis.assistants.ollama.Ollama;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OllamaTest extends Ollama {

    private OllamaTest() {
        super(new Properties());
    }

    @Test
    void testContextPassing() throws JsonProcessingException {
        HttpResponse<String> response = new HttpResponseMock();
        // reads the content AND saves the context
        this.getContent(response);

        assertEquals(List.of(1, 2, 3), this.getContext());

        // create the next request which should contain the context
        String requestBody = this.createRequestBody("prompt");
        assertTrue(requestBody.contains("[1,2,3]"), "The context is not found in the next request.");
    }

    private class HttpResponseMock implements HttpResponse<String> {
        @Override
        public int statusCode() {
            return 0;
        }

        @Override
        public HttpRequest request() {
            return null;
        }

        @Override
        public Optional<java.net.http.HttpResponse<java.lang.String>> previousResponse() {
            return Optional.empty();
        }

        @Override
        public HttpHeaders headers() {
            return null;
        }

        @Override
        public java.lang.String body() {
            try {
                return IntegrationTest.responseWithCode("Error");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Optional<SSLSession> sslSession() {
            return Optional.empty();
        }

        @Override
        public URI uri() {
            return null;
        }

        @Override
        public HttpClient.Version version() {
            return null;
        }
    }
}
