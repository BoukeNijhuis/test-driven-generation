package nl.boukenijhuis;

import nl.boukenijhuis.dto.CodeContainer;
import nl.boukenijhuis.dto.InputContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static nl.boukenijhuis.Utils.addToClassLoader;
import static nl.boukenijhuis.Utils.compileFiles;
import static nl.boukenijhuis.Utils.createTemporaryFile;


public class Generator {

    public static void main(String[] args) {
        try {
            // start a generator and inject an AI assistant
            new Generator().run(new ChatGpt(), new TestRunner(), args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // TODO: create a Bard version as well
    }

    public void run(AIAssistant assistant, TestRunner testRunner, String[] args) throws IOException {

        // sanitize and provide default inputs
        InputContainer inputContainer = InputContainer.build(args);

        // get solution filename and content
        CodeContainer codeContainer = callAssistant(assistant, inputContainer);

        // create the solution file in the temp directory
        Path solutionFilePath = createTemporaryFile(inputContainer, codeContainer);

        // copy the test file to the temp directory
        Path testFileNamePath = inputContainer.getInputFile().getFileName();
        Path destinationFilePath = inputContainer.getOutputDirectory().resolve(codeContainer.getPackageName()).resolve(testFileNamePath);
        Files.copy(inputContainer.getInputFile(), destinationFilePath);

        // compile the solution file and the test source file
        compileFiles(solutionFilePath, destinationFilePath);

        // add all compiled Java files to class loader
        addToClassLoader(inputContainer.getOutputDirectory());

        // run the test
        TestRunner.TestInfo testInfo = testRunner.runTestFile(inputContainer);
        String format = String.format("Found: %d, succeeded: %d", testInfo.found(), testInfo.succeeded());
        System.out.println(format);

        // if failing tests, provide the error to the AI assistant (and get new content)

        // if successful test, stop
    }



    private static CodeContainer callAssistant(AIAssistant assistant, InputContainer inputContainer) {
        CodeContainer response;
        try {
            response = assistant.call(inputContainer.getInputFile());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response;
    }



}
