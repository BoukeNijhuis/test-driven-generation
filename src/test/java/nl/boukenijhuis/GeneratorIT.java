package nl.boukenijhuis;

import nl.boukenijhuis.dto.CodeContainer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratorIT extends IntegrationTest {

    @Test
    public void happyFlowWithPackage() throws IOException {
        String packageName = "example";
        String inputFile = "src/test/resources/input/PrimeNumberGeneratorTest.java";
        String outputFileName = "PrimeNumberGenerator.java";
        String outputFileContent = readFile("expected/PrimeNumberGenerator.java");

        happyFlow(packageName, inputFile, outputFileName, outputFileContent);
    }

    @Test
    public void happyFlowWithoutPackage() throws IOException {

        String packageName = "";
        String inputFile = "src/test/resources/input/PrimeNumberGeneratorTestWithoutPackage.java";
        String outputFileName = "PrimeNumberGeneratorWithoutPackage.java";
        String outputFileContent = readFile("expected/PrimeNumberGeneratorWithoutPackage.java");

        happyFlow(packageName, inputFile, outputFileName, outputFileContent);
    }

    public void happyFlow(String packageName, String inputFile, String outputFileName, String outputFileContent) throws IOException {
        Path tempDirectory = Files.createTempDirectory("test");
        String[] args = {inputFile, tempDirectory.toString()};
        TestRunner testRunner = new TestRunner();
        new Generator().run(new ChatGptTest(outputFileName, outputFileContent), testRunner, args);

        // check if the file is created with correct content
        Path outputFilePath = tempDirectory.resolve(packageName).resolve(outputFileName);
        assertTrue(Files.isRegularFile(outputFilePath));
        assertEquals(outputFileContent, Files.readString(outputFilePath));

        // check the output of the testrunner
        TestRunner.TestInfo latestTestInfo = testRunner.getLatestTestInfo();
        assertEquals(1, latestTestInfo.found());
        assertEquals(1, latestTestInfo.succeeded());
    }

    record ChatGptTest(String outputFileName, String outputFileContent) implements AIAssistant {

        @Override
        public CodeContainer call(Path testFile) {
            return new CodeContainer(outputFileName, outputFileContent);
        }
    }

}