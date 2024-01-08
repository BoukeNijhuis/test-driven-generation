package nl.boukenijhuis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.boukenijhuis.dto.ChatGptRequest;
import nl.boukenijhuis.dto.ChatGptResponse;
import nl.boukenijhuis.dto.CodeContainer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatGpt implements AIAssistant {

    // TODO change in properties
    private static final int maxTokens = 600;
    private static final String apiKey = "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";
    private String server = "https://api.openai.com";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient client = HttpClient.newHttpClient();

    public CodeContainer call(Path testFile) throws IOException, InterruptedException {

        String prompt = "Implement the class under test. %n%n%s";
        String promptWithFile = String.format(prompt, readFile(testFile));
        String requestBody = createRequestBody(promptWithFile);
        HttpRequest request = getHttpRequest(requestBody);

        try {
            String javaContent = "";
            int attempts = 0;
            while (javaContent.isBlank() && attempts < 5) {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String content = getContent(response);
                javaContent = extractJavaContent(content);
                attempts++;
            }
            return new CodeContainer(extractFileName(testFile), javaContent, attempts);
        } catch (IOException | InterruptedException e) {
            throw e;
        }
    }

    private String extractFileName(Path testFile) {
        return testFile.toFile().getName().replace("Test", "");
    }

    private String extractJavaContent(String content) {
        Pattern pattern = Pattern.compile("```java(.*?)```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            // No match found, return empty string or handle as needed
            return "";
        }
    }

    private static String getContent(HttpResponse<String> response) throws JsonProcessingException {
        ChatGptResponse chatGptResponse = objectMapper.readValue(response.body(), ChatGptResponse.class);
        return chatGptResponse.choices().get(0).message().content();
    }

    private String readFile(Path file) {
        try {
            return Files.readString(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest getHttpRequest(String inputBody) {

        try {
            return HttpRequest.newBuilder()
                    .uri(new URI(server + "/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(inputBody))
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static String createRequestBody(String prompt) {
        List<ChatGptRequest.MessageDTO> messageList = List.of(new ChatGptRequest.MessageDTO("user", prompt));
        ChatGptRequest chatGptRequest = new ChatGptRequest("gpt-4", messageList, maxTokens);

        try {
            return objectMapper.writeValueAsString(chatGptRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void setServer(String server) {
        this.server = server;
    }
}