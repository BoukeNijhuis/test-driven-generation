package nl.boukenijhuis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class IntegrationTest {

    /**
     * Reads the contents of a file and returns it as a string.
     *
     * @param fileName the name of the file to read (give path from resources/)
     * @return the contents of the file as a string
     * @throws IOException if an I/O error occurs
     */
    protected String readFile(String fileName) throws IOException {
        try (var in = getClass().getResourceAsStream("/" + fileName)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (NullPointerException e) {
            throw new RuntimeException(String.format("The file '%s' cannot be found in the resources directory.", fileName));
        }
    }
}
