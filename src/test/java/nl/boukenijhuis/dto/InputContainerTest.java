package nl.boukenijhuis.dto;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InputContainerTest {

    @Test
    public void throwErrorWhenNoArguments() {
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            InputContainer.build(new String[0]);
        });
        assertEquals(runtimeException.getMessage(), "No JUnit 5 test file provided as command-line argument.");
    }

    @Test
    public void throwErrorWhenFirstArgumentsIsNoFile() {
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            InputContainer.build(new String[] { "non-existent-file" });
        });
        assertEquals(runtimeException.getMessage(), "File [non-existent-file] is not a file.");
    }

    @Test
    public void throwErrorWhenSecondArgumentsIsNoDirectory() {
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            InputContainer.build(new String[] { "src/test/resources/input/OddEvenTest.java", "non-existent-directory" });
        });
        assertEquals(runtimeException.getMessage(), "Directory [non-existent-directory] is not an empty directory.");

    }

    @Test
    public void throwErrorWhenSecondArgumentsIsNotAnEmptyDirectory() {
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            InputContainer.build(new String[] { "src/test/resources/input/OddEvenTest.java", "src/test/resources/input" });
        });
        assertEquals(runtimeException.getMessage(), "Directory [src/test/resources/input] is not an empty directory.");

    }

    @Test
    public void happyFlow() throws IOException {
        InputContainer inputContainer = InputContainer.build(new String[]{"src/test/resources/input/OddEvenTest.java"});
        assertTrue(Files.isDirectory(inputContainer.getOutputDirectory()));
    }

}