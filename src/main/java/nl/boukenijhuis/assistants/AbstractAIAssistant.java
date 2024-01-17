package nl.boukenijhuis.assistants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.boukenijhuis.dto.CodeContainer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractAIAssistant implements AIAssistant {

    protected static final ObjectMapper objectMapper = new ObjectMapper();
    protected static final HttpClient client = HttpClient.newHttpClient();
    protected Properties properties;

    public AbstractAIAssistant(Properties properties) {
        this.properties = properties;
    }

    public CodeContainer call(Path testFile) throws IOException, InterruptedException {
        String prompt = "Implement the class under test. %n%n%s";
        String promptWithFile = String.format(prompt, readFile(testFile));

        try {
            String requestBody = createRequestBody(promptWithFile);
            HttpRequest request = getHttpRequest(requestBody, getPropertyPrefix());
            String javaContent = "";
            int attempts = 0;
            while (javaContent.isBlank() && attempts < 5) {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body());
                String content = getContent(response);
                javaContent = extractJavaContent(content);
                attempts++;
            }
            return new CodeContainer(extractFileName(testFile), javaContent, attempts);
        } catch (IOException | InterruptedException e) {
            throw e;
        }
    }

    protected abstract String getPropertyPrefix();

    protected abstract String createRequestBody(String promptWithFile) throws JsonProcessingException;

    protected abstract String getContent(HttpResponse<String> response) throws JsonProcessingException;

    protected String extractFileName(Path testFile) {
        return testFile.toFile().getName().replace("Test", "");
    }

    protected String extractJavaContent(String content) {
        Pattern pattern = Pattern.compile("```java(.*?)```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            // No match found, return empty string or handle as needed
            return "";
        }
    }

    protected String readFile(Path file) {
        try {
            return Files.readString(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected HttpRequest getHttpRequest(String inputBody, String propertyPrefix) {
        try {
            String apiKey = (String) properties.get(propertyPrefix + ".api-key");
            String server = (String) properties.get(propertyPrefix + ".server");
            String url = (String) properties.get(propertyPrefix + ".url");
            return HttpRequest.newBuilder()
                    .uri(new URI(server + url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(inputBody))
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
