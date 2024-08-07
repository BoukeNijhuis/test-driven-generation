package nl.boukenijhuis;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import nl.boukenijhuis.assistants.anthropic.Anthropic;
import nl.boukenijhuis.dto.CodeContainer;
import nl.boukenijhuis.dto.PreviousRunContainer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static nl.boukenijhuis.UtilsTest.normalizeLineSeparators;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WireMockTest(httpPort = 8089)
public class AnthropicIT extends IntegrationTest {

    private final String RETRY_SCENARIO = "retry";
    private String SECOND_REPLY = "second";

    @Test
    public void integrationTest() throws IOException, InterruptedException {
        // first reply
        String path = "/v1/messages";
        stubFor(post(path)
                .inScenario(RETRY_SCENARIO)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(ok(readFile("stub/anthropic/stub_without_code.json")))
                .willSetStateTo(SECOND_REPLY));

        // second reply
        stubFor(post(path)
                .inScenario(RETRY_SCENARIO)
                .whenScenarioStateIs(SECOND_REPLY)
                .willReturn(ok(readFile("stub/anthropic/stub_with_working_code.json"))));

        Properties properties = new Properties();
        properties.setProperty("anthropic.server", "http://localhost:8089");
        properties.setProperty("anthropic.url", path);
        properties.setProperty("anthropic.maxTokens", "600");
        properties.setProperty("anthropic.apiKey", "apiKey");
        properties.setProperty("anthropic.timeout", "30");
        var aiAssistant = new Anthropic(properties);

        CodeContainer response = aiAssistant.call(Path.of("src", "test", "resources", "input", "PrimeNumberGeneratorTest.java"), new PreviousRunContainer());
        assertEquals(normalizeLineSeparators(readFile("expected/anthropic/PrimeNumberGenerator.java")), response.getContent());
        assertEquals("PrimeNumberGenerator.java", response.getFileName());
        assertEquals(2, response.getAttempts());
    }
}
