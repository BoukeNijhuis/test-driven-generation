package nl.boukenijhuis;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


class UtilsTest {

    @Test
    public void determineProjectFilePath() {
        if (isWindows()) {
            test("src\\test\\org\\example\\UtilsTest.java", "src\\main\\org\\example");
            test("src\\test\\UtilsTest.java", "src\\main");
            test("\\Users\\boukenijhuis\\git\\test-driven-generation-examples\\src\\test\\java\\org\\example\\oddeven\\OddEvenTest.java",
                    "\\Users\\boukenijhuis\\git\\test-driven-generation-examples\\src\\main\\java\\org\\example\\oddeven");
        } else {
            test("src/test/org/example/UtilsTest.java", "src/main/org/example");
            test("src/test/UtilsTest.java", "src/main");
            test("/Users/boukenijhuis/git/test-driven-generation-examples/src/test/java/org/example/oddeven/OddEvenTest.java",
                    "/Users/boukenijhuis/git/test-driven-generation-examples/src/main/java/org/example/oddeven");
        }
    }

    private void test(String input, String output) {
        Path path = Utils.determineProjectParentFilePath(Path.of(input));
        assertEquals(output, path.toString());
    }

    @Test
    public void testIsDirectoryWithinTempDir() {
        // Get the system temporary directory
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

        // Create a path that is within the temporary directory
        Path testPath = tempDir.resolve("testSubDir");

        assertTrue(Utils.isTemporaryDirectory(testPath));
    }

    @Test
    public void testIsDirectoryOutsideTempDir() {
        // Get the system temporary directory
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

        // Determine the root directory based on the OS
        Path rootDir;
        if (isWindows()) {
            rootDir = Paths.get("C:\\");
        } else {
            rootDir = Paths.get("/");
        }

        // Create a path that is outside the temporary directory
        Path testPath = rootDir.resolve("outsideTempDir");

        assertFalse(Utils.isTemporaryDirectory(testPath));
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    // is used to convert different types of line endings (Windows, old Mac, Unix) into a single, consistent format (\n)
    static String normalizeLineSeparators(String content) {
        return content.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");
    }



}