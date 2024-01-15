package nl.boukenijhuis;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import nl.boukenijhuis.dto.CodeContainer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WireMockTest(httpPort = 8089)
public class ChatGptIT extends IntegrationTest {

    private final String RETRY_SCENARIO = "retry";
    private String SECOND_REPLY = "second";

    @Test
    public void integrationTest() throws IOException, InterruptedException {
        // first reply
        stubFor(post("/v1/chat/completions")
                .inScenario(RETRY_SCENARIO)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(ok(readFile("stub/stub_without_code.json")))
                .willSetStateTo(SECOND_REPLY));

        // second reply
        stubFor(post("/v1/chat/completions")
                .inScenario(RETRY_SCENARIO)
                .whenScenarioStateIs(SECOND_REPLY)
                .willReturn(ok(readFile("stub/stub_with_working_code.json"))));

        Properties properties = new Properties();
        properties.setProperty("chatgpt.server", "http://localhost:8089");
        properties.setProperty("chatgpt.url", "/v1/chat/completions");
        properties.setProperty("chatgpt.maxTokens", "600");
        properties.setProperty("chatgpt.apiKey", "apiKey");
        ChatGpt chatGpt = new ChatGpt(properties);

        CodeContainer response = chatGpt.call(Path.of("src", "test", "resources", "input", "PrimeNumberGeneratorTest.java"));
        assertEquals(readFile("expected/PrimeNumberGenerator.java"), response.content());
        assertEquals("PrimeNumberGenerator.java", response.fileName());
        assertEquals(2, response.attempts());
    }

}
