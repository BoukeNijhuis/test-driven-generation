package nl.boukenijhuis.assistants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.boukenijhuis.ClassNameNotFoundException;
import nl.boukenijhuis.dto.CodeContainer;
import nl.boukenijhuis.dto.PreviousRunContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractAIAssistant implements AIAssistant {

    // we initialize this later so we can use generic logging
    protected static Logger LOG;

    protected static final ObjectMapper objectMapper = new ObjectMapper();
    // TODO make configurable && use something like a response timeout
    protected static final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    protected Properties properties;

    public AbstractAIAssistant(Properties properties) {
        LOG = LogManager.getLogger(AbstractAIAssistant.class);
        String model = properties.getProperty(getPropertyPrefix() + ".model");
        LOG.debug("Family: {}, model: {}", getPropertyPrefix(), model);
        this.properties = properties;
    }

    public CodeContainer call(Path testFile, PreviousRunContainer previousRunContainer) throws IOException, InterruptedException {

        // TODO introduce own properties object that holds defaults?
        int maxInternalAttempts = Integer.parseInt(properties.getProperty("retries", "5"));

        String javaContent = "";
        int internalAttempts = 0;

        // use input from previous run when available
        String inputPreviousRun = previousRunContainer.getInput();

        while (++internalAttempts <= maxInternalAttempts) {

            LOG.info("Code loop attempt: {}", internalAttempts);

            String prompt;
            if (inputPreviousRun != null && !inputPreviousRun.isBlank()) {
                prompt = getPromptWithError(inputPreviousRun);
            } else {
                prompt = getPromptWithFile(testFile);
            }

            LOG.debug("Prompt: {}", prompt);

            String requestBody = createRequestBody(prompt);
            HttpRequest request = getHttpRequest(requestBody, getPropertyPrefix());

            // TODO: add a time out of ten seconds
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String content = getContent(response);
            LOG.debug("Received answer: \n{}", content);
            javaContent = extractJavaContent(content);
            if (javaContent != null) {

                // we do not want a test, but an implementation
                if (javaContent.contains("@Test") || javaContent.contains("@org.junit.jupiter.api.Test")) {
                    inputPreviousRun = "You gave a test, but I asked for production Java code!";
                    continue;
                }

                try {
                    return new CodeContainer(javaContent, internalAttempts);
                } catch (ClassNameNotFoundException e) {
                    // do nothing == continue
                }
            }

            // TODO: should the context be cleared if no java is found?

        }
        // no solution found
        String message = String.format("Could not find a solution after %s attempts.", maxInternalAttempts);
        throw new RuntimeException(message);
    }

    private String getPromptWithError(String inputPreviousRun) {
        return "Provide a new implementation that fixes the following error: " + inputPreviousRun;
    }

    // TODO per assistant?
    private String getPromptWithFile(Path testFile) {

        String prompt = properties.getProperty(getPropertyPrefix() + ".prompt");
        if (prompt == null) {
            prompt = "You are a professional Java developer. Give me a JAVA IMPLEMENTATION that will pass this test. Do not respond with a test. Give me only complete code and no snippets. Include imports and use the right package. %n%n%s";
        }
        return String.format(prompt, readFile(testFile));
    }

    protected abstract String getPropertyPrefix();

    protected abstract String createRequestBody(String promptWithFile) throws JsonProcessingException;

    protected abstract String getContent(HttpResponse<String> response) throws JsonProcessingException;

    protected String extractFileName(Path testFile) {
        return testFile.toFile().getName().replace("Test", "");
    }

    // TODO per assistant?
    protected String extractJavaContent(String content) {

        List<String> stringList = List.of(
                "```java(.*?)```",
                "```Java(.*?)```",
                "\\[Java\\](.*?)\\[/Java\\]",
                "\\[Java Code\\](.*?)\\[/Java Code\\]",
                "```(.*?)```"
        );

        for (String s : stringList) {
            String javaContent = extractJavaContent(content, Pattern.compile(s, Pattern.DOTALL));
            if (javaContent != null) {
                return javaContent;
            }
        }

        // answer could be code only
        content = content.trim();
        if (content.startsWith("package") || content.startsWith("import")) {
            // it is probably code
            return content;
        }

        // nothing found
        return null;
    }

    private String extractJavaContent(String content, Pattern pattern) {
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            return null;
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
            int timeout = Integer.parseInt((String) properties.get(propertyPrefix + ".timeout"));
            return HttpRequest.newBuilder()
                    .uri(new URI(server + url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(inputBody))
                    .timeout(Duration.ofSeconds(timeout))
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
