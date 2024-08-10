package nl.boukenijhuis.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ArgumentContainerTest {

    @Test
    void happyFlow() {
        var args = List.of("--test-file", "tf", "--working-directory", "wd", "--family", "f", "--model", "m");
        var stringArray = args.toArray(new String[0]);
        ArgumentContainer argumentContainer = new ArgumentContainer(stringArray);

        assertEquals("tf", argumentContainer.getTestFile());
        assertEquals("wd", argumentContainer.getWorkingDirectory());
        assertEquals("f", argumentContainer.getFamily());
        assertEquals("m", argumentContainer.getModel());
    }

    @Test
    void provideOnlyTestFileAndFamily() {
        var args = List.of("--test-file", "tf", "--family", "f");
        var stringArray = args.toArray(new String[0]);
        ArgumentContainer argumentContainer = new ArgumentContainer(stringArray);

        assertEquals("tf", argumentContainer.getTestFile());
        assertNull(argumentContainer.getWorkingDirectory());
        assertEquals("f", argumentContainer.getFamily());
        assertNull(argumentContainer.getModel());
    }

    @Test
    void provideOnlyOneArgument() {
        var args = List.of("tf");
        var stringArray = args.toArray(new String[0]);
        ArgumentContainer argumentContainer = new ArgumentContainer(stringArray);

        assertEquals("tf", argumentContainer.getTestFile());
        assertNull(argumentContainer.getWorkingDirectory());
        assertNull(argumentContainer.getFamily());
        assertNull(argumentContainer.getModel());
    }
}