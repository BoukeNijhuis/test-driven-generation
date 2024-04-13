package nl.boukenijhuis.dto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class InputContainer {
    private static final Logger LOG = LogManager.getLogger(InputContainer.class);
    private final Path inputFile;
    private final Path outputDirectory;

    private InputContainer(Path inputFile, Path outputDirectory) {
        this.inputFile = inputFile;
        this.outputDirectory = outputDirectory;
    }

    public static InputContainer build(ArgumentContainer argumentContainer) throws IOException {


        // check if the first argument is a file
        String testFile = argumentContainer.getTestFile();
        Path inputFile = Path.of(testFile);
        if (!Files.isRegularFile(inputFile)) {
            throw new RuntimeException("File [" + testFile + "] is not a file.");
        }

        // check / create the output directory
        String workingDirectory = argumentContainer.getWorkingDirectory();
        Path outputDirectory;
        if (workingDirectory != null) {
            outputDirectory = Path.of(workingDirectory);

            if (!isAnEmptyDirectory(outputDirectory)) {
                throw new RuntimeException("Directory [" + workingDirectory + "] is not an empty directory.");
            }
        } else {
            // create a temp dir
            outputDirectory = Files.createTempDirectory("generator");
        }
        LOG.debug("Output directory: {}", outputDirectory);

        // TODO: does the input compile (difficult to prove without implementation)

        return new InputContainer(inputFile, outputDirectory);
    }

    private static boolean isAnEmptyDirectory(Path path) throws IOException {
        return Files.isDirectory(path) &&
                !Files.newDirectoryStream(path).iterator().hasNext();
    }

    public Path getInputFile() {
        return inputFile;
    }

    public Path getOutputDirectory() {
        return outputDirectory;
    }
}
