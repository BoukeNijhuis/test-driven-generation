package nl.boukenijhuis;

import nl.boukenijhuis.assistants.AIAssistant;
import nl.boukenijhuis.dto.ArgumentContainer;
import nl.boukenijhuis.dto.CodeContainer;
import nl.boukenijhuis.dto.PreviousRunContainer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratorIT extends IntegrationTest {

    @Test
    public void chatGptHappyFlowWithoutPackage() throws IOException {

        String packageName = "";
        String inputFile = "src/test/resources/input/PrimeNumberGeneratorTestWithoutPackage.java";
        String outputFileName = "PrimeNumberGeneratorWithoutPackage.java";
        String outputFileContent = readFile("expected/chatgpt/" + outputFileName);

        happyFlow(packageName, inputFile, outputFileName, outputFileContent);
    }

    // TODO rename files to "WithSingleLevelPackage"
    @Test
    public void chatGptHappyFlowWithSinglePackage() throws IOException {
        String packageName = "example";
        String inputFile = "src/test/resources/input/PrimeNumberGeneratorTest.java";
        String outputFileName = "PrimeNumberGenerator.java";
        String outputFileContent = readFile("expected/chatgpt/" + outputFileName);

        happyFlow(packageName, inputFile, outputFileName, outputFileContent);
    }

    @Test
    public void chatGptHappyFlowWithMultipleLevelPackage() throws IOException {
        String packageName = "org.example";
        String inputFile = "src/test/resources/input/PrimeNumberGeneratorTestWithMultipleLevelPackage.java";
        String outputFileName = "PrimeNumberGeneratorWithMultipleLevelPackage.java";
        String outputFileContent = readFile("expected/chatgpt/" + outputFileName);

        happyFlow(packageName, inputFile, outputFileName, outputFileContent);
    }

    @Test
    public void llama2Test() throws IOException {

        String packageName = "example";
        String inputFile = "src/test/resources/input/PrimeNumberGeneratorTest.java";
        String outputFileName = "PrimeNumberGenerator.java";
        String outputFileContent = readFile("expected/ollama/" + outputFileName);

        happyFlow(packageName, inputFile, outputFileName, outputFileContent);
    }

    public void happyFlow(String packageName, String inputFile, String outputFileName, String outputFileContent) throws IOException {
        Path tempDirectory = Files.createTempDirectory("test");
        String[] args = {"--test-file", inputFile, "--working-directory", tempDirectory.toString()};
        TestRunner testRunner = new TestRunner();
        new Generator().run(new StubAssistant(outputFileContent), testRunner, new ArgumentContainer(args));

        // check if the file is created with correct content
        String packageDirectories = packageName.replace(".", "/");
        Path outputFilePath = tempDirectory.resolve(packageDirectories).resolve(outputFileName);
        assertTrue(Files.isRegularFile(outputFilePath));
        assertEquals(outputFileContent, Files.readString(outputFilePath));

        // check the output of the testrunner
        TestRunner.TestInfo latestTestInfo = testRunner.getLatestTestInfo();
        assertEquals(1, latestTestInfo.found());
        assertEquals(1, latestTestInfo.succeeded());
    }


    record StubAssistant(String outputFileContent) implements AIAssistant {

        @Override
        public CodeContainer call(Path testFile, PreviousRunContainer previousRunContainer)  {
            try {
                return new CodeContainer(outputFileContent);
            } catch (ClassNameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

}