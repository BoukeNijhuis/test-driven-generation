package nl.boukenijhuis;

import nl.boukenijhuis.dto.CodeContainer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratorIT extends IntegrationTest {

    String outputFileName = "PrimeNumberGenerator.java";
    String outputFileContent;

    // TODO create test without packages

    @Test
    public void happyFlow() throws IOException {
        outputFileContent = readFile("expected/PrimeNumberGenerator.java");

        Path tempDirectory = Files.createTempDirectory("test");
        String inputFile = "src/test/resources/input/PrimeNumberGeneratorTest.java";
        String[] args = {inputFile, tempDirectory.toString()};
        TestRunner testRunner = new TestRunner();
        new Generator().run(new ChatGptTest(), testRunner, args);

        // check if the file is created with correct content
        Path outputFilePath = tempDirectory.resolve("input").resolve(outputFileName);
        assertTrue(Files.isRegularFile(outputFilePath));
        assertEquals(outputFileContent, Files.readString(outputFilePath));

        // check the output of the testrunner
        TestRunner.TestInfo latestTestInfo = testRunner.getLatestTestInfo();
        assertEquals(1, latestTestInfo.found());
        assertEquals(1, latestTestInfo.succeeded());
    }

    class ChatGptTest implements AIAssistant {

        @Override
        public CodeContainer call(Path testFile) {
            return new CodeContainer(outputFileName, outputFileContent);
        }
    }

}