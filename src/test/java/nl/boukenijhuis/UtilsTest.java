package nl.boukenijhuis;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTest {

    @Test
    public void determineProjectFilePath() {

        test("src/test/org/example/UtilsTest.java", "src/main/org/example");
        test("src/test/UtilsTest.java", "src/main");
        test("/Users/boukenijhuis/git/test-driven-generation-examples/src/test/java/org/example/oddeven/OddEvenTest.java",
                "/Users/boukenijhuis/git/test-driven-generation-examples/src/main/java/org/example/oddeven");
    }

    private void test(String input, String output) {
        Path path = Utils.determineProjectParentFilePath(Path.of(input));
        assertEquals(output, path.toString());
    }

}