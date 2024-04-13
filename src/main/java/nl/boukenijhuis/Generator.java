package nl.boukenijhuis;

import nl.boukenijhuis.assistants.AIAssistant;
import nl.boukenijhuis.assistants.ollama.Ollama;
import nl.boukenijhuis.dto.ArgumentContainer;
import nl.boukenijhuis.dto.CodeContainer;
import nl.boukenijhuis.dto.InputContainer;
import nl.boukenijhuis.dto.PreviousRunContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static nl.boukenijhuis.Utils.addToClassLoader;
import static nl.boukenijhuis.Utils.compileFiles;
import static nl.boukenijhuis.Utils.createTemporaryFile;
import static nl.boukenijhuis.Utils.determineProjectParentFilePath;

public class Generator {

    private static final Logger LOG = LogManager.getLogger(Generator.class);
    private final List<String> dependencies;

    public Generator() {
        this(new ArrayList<>());
    }

    public Generator(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public static void main(String[] args) {
        try {
            // read the properties
            Properties properties = new Properties();
            properties.load(Generator.class.getResourceAsStream("/test-driven-generation.properties"));

            // parse the command line arguments
            ArgumentContainer argumentContainer = new ArgumentContainer(args);

            // start a generator and inject an AI assistant
            new Generator().run(new Ollama(properties), new TestRunner(), argumentContainer);
        } catch (Exception e) {
            LOG.info(e);
        }
    }

    // returns true when a solution is found, false otherwise (used in maven plugin)
    public boolean run(AIAssistant assistant, TestRunner testRunner, ArgumentContainer argumentContainer) throws IOException {

        // sanitize and provide default inputs
        InputContainer inputContainer = InputContainer.build(argumentContainer);

        // object for storing test information
        TestRunner.TestInfo testInfo = null;
        int externalAttempts = 0;
        // TODO get from properties, static global properties object?
        int externalMaxRetries = 5;
        Path tempTestFilePath = null;
        Path solutionFilePath = null;
        CodeContainer codeContainer = null;
        PreviousRunContainer previousRunContainer = new PreviousRunContainer();

        do {
            LOG.info("Test loop attempt: {}", ++externalAttempts);

            // get solution filename and content
            codeContainer = callAssistant(assistant, inputContainer, previousRunContainer);

            // create the solution file in the temp directory
            solutionFilePath = createTemporaryFile(inputContainer, codeContainer);

            // copy the test file to the temp directory
            try {
                // test package directories do not have to equal the implementation package directories
                CodeContainer testCodeContainer = new CodeContainer(Files.readString(inputContainer.getInputFile()));
                tempTestFilePath = Utils.createTemporaryFile(inputContainer, testCodeContainer);
            } catch (ClassNameNotFoundException e) {
                throw new RuntimeException(e);
            }

            // compile the solution file and the test source file
            var compilationContainer = compileFiles(dependencies, solutionFilePath, tempTestFilePath);
            if (!compilationContainer.compilationSuccessful()) {
                // give the error to the AI assistant
                previousRunContainer = createPreviousRunContainer(compilationContainer.errorMessage());
                continue;
            }

            // add all compiled Java files to class loader (including the dependencies)
            addToClassLoader(inputContainer.getOutputDirectory(), this.dependencies);

            // run the test
            testInfo = testRunner.runTestFile(inputContainer);
            LOG.info("Tests found: {}, succeeded: {}", testInfo.found(), testInfo.succeeded());

            // if failing tests, provide the error to the AI assistant (and get new content)
            if (solutionNotFound(testInfo)) {
                // give the error to the AI assistant
                previousRunContainer = createPreviousRunContainer(testInfo.errorOutput());
            }

        } while (solutionNotFound(testInfo) && externalAttempts <= externalMaxRetries - 1);

        if (solutionNotFound(testInfo)) {
            LOG.info("No solution found.");
            return false;
        } else {

            // copy the file to the project (based on the path of the test file)
            Path projectFileParentPath = determineProjectParentFilePath(inputContainer.getInputFile());
            Path projectFilePath = projectFileParentPath.resolve(codeContainer.getFileName());
            // only copy when the project file path exists
            if (Files.exists(projectFileParentPath)) {
                solutionFilePath = Files.copy(solutionFilePath, projectFilePath, REPLACE_EXISTING);
            }

            LOG.info("Solution found: {}", solutionFilePath);
            return true;
        }
    }

    private PreviousRunContainer createPreviousRunContainer(String error) {
        LOG.debug("Received error: {}", error);
        return new PreviousRunContainer(error);
    }

    private boolean solutionNotFound(TestRunner.TestInfo testInfo) {
        return testInfo == null || testInfo.found() == 0 || testInfo.succeeded() != testInfo.found();
    }

    private static CodeContainer callAssistant(AIAssistant assistant, InputContainer inputContainer, PreviousRunContainer previousRunContainer) {
        CodeContainer response;
        try {
            response = assistant.call(inputContainer.getInputFile(), previousRunContainer);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}
