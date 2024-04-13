package nl.boukenijhuis;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import nl.boukenijhuis.assistants.ollama.Ollama;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
    public void testCorrectClassLoading() throws IOException {

        String firstResponse = responseWithCode("Error");
        String secondResponse = responseWithCode(readFile("stub/ollama/code/primenumber/PrimeNumberGenerator_faulty.java"));
        String thirdResponse = responseWithCode(readFile("stub/ollama/code/primenumber/PrimeNumberGenerator_correct.java"));
        threeStubs(firstResponse, secondResponse, thirdResponse);

        // TODO can I fix this without creating a temp directory in this test?
        Path tempDirectory = Files.createTempDirectory("test");
        String inputFile = "src/test/resources/input/PrimeNumberGeneratorTest.java";
        String[] args = {"--test-file", inputFile, "--working-directory",  tempDirectory.toString()};
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
    public void testImplementationReloading() throws IOException {
        String firstResponse = responseWithCode(readFile("stub/ollama/code/uppercaser/Uppercaser0.java"));
        String secondResponse = responseWithCode(readFile("stub/ollama/code/uppercaser/Uppercaser1.java"));
        String thirdResponse = responseWithCode(readFile("stub/ollama/code/uppercaser/Uppercaser2.java"));
        threeStubs(firstResponse, secondResponse, thirdResponse);

        // TODO can I fix this without creating a temp directory in this test?
        Path tempDirectory = Files.createTempDirectory("test");
        String inputFile = "src/test/resources/input/UppercaserTest.java";
        String[] args = {"--test-file", inputFile, "--working-directory",  tempDirectory.toString()};
        TestRunner testRunner = new TestRunner();
        new Generator().run(new Ollama(properties), testRunner, args);

        // check the output of the testrunner
        TestRunner.TestInfo latestTestInfo = testRunner.getLatestTestInfo();
        assertEquals(3, latestTestInfo.found());
        assertEquals(3, latestTestInfo.succeeded());
    }

    // this test will fail if an existing implementation is already on the classpath
    @Test
    public void testImplementationReloadingWhileThereIsAlreadyAnExistingImplementation() throws IOException {
        String firstResponse = responseWithCode(readFile("stub/ollama/code/uppercaser/Uppercaser0.java"));
        String secondResponse = responseWithCode(readFile("stub/ollama/code/uppercaser/Uppercaser1.java"));
        String thirdResponse = responseWithCode(readFile("stub/ollama/code/uppercaser/Uppercaser2.java"));
        threeStubs(firstResponse, secondResponse, thirdResponse);

        // create a class file in the external classpath
        Path externalClassPath = Files.createTempDirectory("externalClassPath");
        var existingImpl = Path.of("/Users/boukenijhuis/git/test-driven-generation/src/test/resources/stub/ollama/code/uppercaser/externalClassPath/Uppercaser.java");
        existingImpl = Files.copy(existingImpl, externalClassPath.resolve("Uppercaser.java"));
        Utils.compileFiles(List.of(), existingImpl);
        String externalClassPathString = externalClassPath.toString();
        List<String> inputClassPathStringList = List.of(externalClassPathString);

        // TODO can I fix this without creating a temp directory in this test?
        Path tempDirectory = Files.createTempDirectory("test");
        String inputFile = "src/test/resources/input/UppercaserTest.java";
        String[] args = {"--test-file", inputFile, "--working-directory",  tempDirectory.toString()};
        TestRunner testRunner = new TestRunner();
        new Generator(inputClassPathStringList).run(new Ollama(properties), testRunner, args);

        // check the output of the testrunner
        TestRunner.TestInfo latestTestInfo = testRunner.getLatestTestInfo();
        assertEquals(3, latestTestInfo.found());
        assertEquals(3, latestTestInfo.succeeded());
    }

    private void threeStubs(String first, String second, String third) throws IOException {
        // first reply
        stubFor(post(path)
                .inScenario(RETRY_SCENARIO)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(ok(first))
                .willSetStateTo(SECOND_REPLY));

        // second reply
        stubFor(post(path)
                .inScenario(RETRY_SCENARIO)
                .whenScenarioStateIs(SECOND_REPLY)
                .willReturn(ok(second))
                .willSetStateTo(THIRD_REPLY));

        // third reply
        stubFor(post(path)
                .inScenario(RETRY_SCENARIO)
                .whenScenarioStateIs(THIRD_REPLY)
                .willReturn(ok(third)));
    }

}
