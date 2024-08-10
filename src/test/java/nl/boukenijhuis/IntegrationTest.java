package nl.boukenijhuis;

import nl.boukenijhuis.dto.PropertiesContainer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Properties;

public class IntegrationTest {

    /**
     * Reads the contents of a file and returns it as a string.
     *
     * @param fileName the name of the file to read (give path from resources/)
     * @return the contents of the file as a string
     * @throws IOException if an I/O error occurs
     */
    protected static String readFile(String fileName) throws IOException {
        String normalizedFileName = Paths.get(fileName).toString();
        try (var in = IntegrationTest.class.getResourceAsStream("/" + normalizedFileName)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (NullPointerException e) {
            throw new RuntimeException(String.format("The file '%s' cannot be found in the resources directory.", fileName));
        }
    }

    // use this when you want a request containing a piece of code
    public static String responseWithCode(String code) throws IOException {
        return java.lang.String.format(readFile("stub/ollama/stub_with_input_parameter.json"), convertToJsonValue(code));
    }

    // use this when you want a request containing a piece of text
    protected String responseWithText(String text) throws IOException {
        return String.format(readFile("stub/ollama/stub_with_empty_response.json"), convertToJsonValue(text));
    }

    // escape double qoutes and convert end of lines
    private static String convertToJsonValue(String input) {
        return input.replace("\n", "\\n").replace("\"", "\\\"");
    }

    public Properties createProperties(String family, String path) {
        Properties properties = new Properties();
        properties.setProperty(family + ".server", "http://localhost:8089");
        properties.setProperty(family + ".url", path);
        properties.setProperty(family + ".maxTokens", "600");
        properties.setProperty(family + ".apiKey", "apiKey");
        properties.setProperty(family + ".timeout", "30");
        return properties;
    }

    class TestPropertiesContainer extends PropertiesContainer {

        public TestPropertiesContainer(Properties properties, String[] args) {
            super(args);
            this.properties = properties;
        }

        public TestPropertiesContainer(String family, String path) {
            super(new String[] { "--family", family});
            this.properties = createProperties(family, path);
        }
    }
}
