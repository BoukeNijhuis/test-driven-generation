package nl.boukenijhuis.dto;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InputContainerTest {

    @Test
    public void throwErrorWhenFirstArgumentsIsNoFile() {
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            PropertiesContainer argumentContainer = new PropertiesContainer(new String[] { "non-existent-file" });
            InputContainer.build(argumentContainer);
        });
        assertEquals(runtimeException.getMessage(), "File [non-existent-file] is not a file.");
    }

    @Test
    public void throwErrorWhenSecondArgumentsIsNoDirectory() {
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            String[] args = {"--test-file", "src/test/resources/input/OddEvenTest.java", "--working-directory", "non-existent-directory"};
            PropertiesContainer argumentContainer = new PropertiesContainer(args);
            InputContainer.build(argumentContainer);
        });
        assertEquals(runtimeException.getMessage(), "Directory [non-existent-directory] is not an empty directory.");

    }

    @Test
    public void throwErrorWhenSecondArgumentsIsNotAnEmptyDirectory() {
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            String[] args = {"--test-file", "src/test/resources/input/OddEvenTest.java", "--working-directory", "src/test/resources/input"};
            PropertiesContainer argumentContainer = new PropertiesContainer(args);
            InputContainer.build(argumentContainer);
        });
        assertEquals(runtimeException.getMessage(), "Directory [src/test/resources/input] is not an empty directory.");

    }

    @Test
    public void happyFlow() throws IOException {
        PropertiesContainer argumentContainer = new PropertiesContainer(new String[]{"src/test/resources/input/OddEvenTest.java"});
        InputContainer inputContainer = InputContainer.build(argumentContainer);
        assertTrue(Files.isDirectory(inputContainer.getOutputDirectory()));
    }

}