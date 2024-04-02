package nl.boukenijhuis;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import nl.boukenijhuis.assistants.ollama.Ollama;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WireMockTest(httpPort = 8089)
public class RetriesIT extends IntegrationTest {

    private static final String RETRY_SCENARIO = "retry";
    private static final String SECOND_REPLY = "second";
    private static final String THIRD_REPLY = "third";
    private static final Properties properties = new Properties();
    private static final String path = "/api/generate";

    @BeforeAll
    static void beforeAll() throws IOException {
        properties.setProperty("ollama.server", "http://localhost:8089");
        properties.setProperty("ollama.url", path);
        properties.setProperty("ollama.timeout", "30");
    }

    /**
     * This test is created after I encountered a class loading bug. Once a class is loaded it will
     * not load a new implementation, so you have to create a new class loader and make sure that
     * the old class loader is not used anymore.
     */
    @Test
    public void testCorrectClassLoading() throws IOException, InterruptedException {

        // first reply
        stubFor(post(path)
                .inScenario(RETRY_SCENARIO)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(ok(readFile("stub/ollama/stub_without_code.json")))
                .willSetStateTo(SECOND_REPLY));

        // second reply
        stubFor(post(path)
                .inScenario(RETRY_SCENARIO)
                .whenScenarioStateIs(SECOND_REPLY)
                .willReturn(ok(readFile("stub/ollama/stub_with_faulty_code.json")))
                .willSetStateTo(THIRD_REPLY));

        // third reply
        stubFor(post(path)
                .inScenario(RETRY_SCENARIO)
                .whenScenarioStateIs(THIRD_REPLY)
                .willReturn(ok(readFile("stub/ollama/stub_with_working_code.json"))));


        // TODO can I fix this without creating a temp directory in this test?
        Path tempDirectory = Files.createTempDirectory("test");
        String inputFile = "src/test/resources/input/PrimeNumberGeneratorTest.java";
        String[] args = {inputFile, tempDirectory.toString()};
        TestRunner testRunner = new TestRunner();
        new Generator().run(new Ollama(properties), testRunner, args);

        // check if the file is created with correct content
        Path outputFilePath = tempDirectory.resolve("example").resolve("PrimeNumberGenerator.java");
        assertTrue(Files.isRegularFile(outputFilePath));
        String outputFileContent = readFile("expected/ollama/PrimeNumberGenerator.java");
        assertEquals(outputFileContent, Files.readString(outputFilePath));

        // check the output of the testrunner
        TestRunner.TestInfo latestTestInfo = testRunner.getLatestTestInfo();
        assertEquals(1, latestTestInfo.found());
        assertEquals(1, latestTestInfo.succeeded());
    }

    @Test
    public void testImplementationReloading() throws IOException, InterruptedException {
        String firstResponse = responseWithCode(readFile("stub/ollama/code/Uppercaser0.java"));
        String secondResponse = responseWithCode(readFile("stub/ollama/code/Uppercaser1.java"));
        String thirdResponse = responseWithCode(readFile("stub/ollama/code/Uppercaser2.java"));

        // first reply
        stubFor(post(path)
                .inScenario(RETRY_SCENARIO)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(ok(firstResponse))
                .willSetStateTo(SECOND_REPLY));

        // second reply
        stubFor(post(path)
                .inScenario(RETRY_SCENARIO)
                .whenScenarioStateIs(SECOND_REPLY)
                .willReturn(ok(secondResponse))
                .willSetStateTo(THIRD_REPLY));

        // third reply
        stubFor(post(path)
                .inScenario(RETRY_SCENARIO)
                .whenScenarioStateIs(THIRD_REPLY)
                .willReturn(ok(thirdResponse)));

        // TODO can I fix this without creating a temp directory in this test?
        Path tempDirectory = Files.createTempDirectory("test");
        String inputFile = "src/test/resources/input/UppercaserTest.java";
        String[] args = {inputFile, tempDirectory.toString()};
        TestRunner testRunner = new TestRunner();
        new Generator().run(new Ollama(properties), testRunner, args);

        // check the output of the testrunner
        TestRunner.TestInfo latestTestInfo = testRunner.getLatestTestInfo();
        assertEquals(3, latestTestInfo.found());
        assertEquals(3, latestTestInfo.succeeded());
    }

}
