package nl.boukenijhuis;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import nl.boukenijhuis.assistants.chatgpt.ChatGpt;
import nl.boukenijhuis.dto.CodeContainer;
import nl.boukenijhuis.dto.PreviousRunContainer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static nl.boukenijhuis.UtilsTest.normalizeLineSeparators;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WireMockTest(httpPort = 8089)
public class ChatGptIT extends IntegrationTest {

    private final String RETRY_SCENARIO = "retry";
    private String SECOND_REPLY = "second";

    @Test
    public void integrationTest() throws IOException, InterruptedException, ClassNameNotFoundException {
        // first reply
        String path = "/v1/chat/completions";
        stubFor(post(path)
                .inScenario(RETRY_SCENARIO)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(ok(readFile(Paths.get("stub/chatgpt/stub_without_code.json").toString())))
                .willSetStateTo(SECOND_REPLY));

        // second reply
        stubFor(post(path)
                .inScenario(RETRY_SCENARIO)
                .whenScenarioStateIs(SECOND_REPLY)
                .willReturn(ok(readFile(Paths.get("stub/chatgpt/stub_with_working_code.json").toString()))));

        Properties properties = new Properties();
        properties.setProperty("chatgpt.server", "http://localhost:8089");
        properties.setProperty("chatgpt.url", path);
        properties.setProperty("chatgpt.maxTokens", "600");
        properties.setProperty("chatgpt.apiKey", "apiKey");
        properties.setProperty("chatgpt.timeout", "30");
        var aiAssistant = new ChatGpt(properties);

        CodeContainer response = aiAssistant.call(Path.of("src", "test", "resources", "input", "PrimeNumberGeneratorTest.java"), new PreviousRunContainer());
        assertEquals(normalizeLineSeparators(readFile(Paths.get("expected/chatgpt/PrimeNumberGenerator.java").toString())), response.getContent());
        assertEquals("PrimeNumberGenerator.java", response.getFileName());
        assertEquals(2, response.getAttempts());
    }
}
