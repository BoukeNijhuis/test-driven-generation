package nl.boukenijhuis.dto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class InputContainer {
    private static final Logger LOG = LogManager.getLogger();
    private final Path inputFile;
    private final Path outputDirectory;

    private InputContainer(Path inputFile, Path outputDirectory) {
        this.inputFile = inputFile;
        this.outputDirectory = outputDirectory;
    }

    public static InputContainer build(String[] args) throws IOException {
        // check the input
        if (args.length < 1) {
            throw new RuntimeException("No JUnit 5 test file provided as command-line argument.");
        }

        // check if the first argument is a file
        Path inputFile = Path.of(args[0]);
        if (!Files.isRegularFile(inputFile)) {
            throw new RuntimeException(args[0] + " is not a file.");
        }

        // check / create the output directory
        Path outputDirectory;
        if (args.length == 2) {
            outputDirectory = Path.of(args[1]);

            if (!isAnEmptyDirectory(outputDirectory)) {
                throw new RuntimeException(args[1] + " is not an empty directory.");
            }
        } else {
            // create a temp dir
            outputDirectory = Files.createTempDirectory("bouke");
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
