package nl.boukenijhuis.dto;

import nl.boukenijhuis.Generator;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class PropertiesContainer {

    protected Properties properties;
    private ArgumentContainer argumentContainer;
    private String family;

    public PropertiesContainer(String[] args) {
        // read the properties
        properties = new Properties();
        try {
            properties.load(Generator.class.getResourceAsStream("/test-driven-generation.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        updatePropertiesWithArguments(args);
    }

    protected void updatePropertiesWithArguments(String[] args) {
        // parse the command line arguments
        argumentContainer = new ArgumentContainer(args);

        // family is special because it is the prefix in the properties (default to Ollama)
        family = argumentContainer.getFamily();
        if (family == null) {
            family = "ollama";
        }

        updatePropertyIfArgumentProvided(argumentContainer.getServer(), ".server");
        updatePropertyIfArgumentProvided(argumentContainer.getUrl(), ".url");
        // family is special therefor already determined a few lines above
        updatePropertyIfArgumentProvided(argumentContainer.getModel(), ".model");
        updatePropertyIfArgumentProvided(argumentContainer.getMaxTokens(), ".maxTokens");
        updatePropertyIfArgumentProvided(argumentContainer.getTimeout(), ".timeout");
        updatePropertyIfArgumentProvided(argumentContainer.getPrompt(), ".prompt");
    }

    private void updatePropertyIfArgumentProvided(String value, String propertyPostfix) {
        // update the properties when a value is provided as argument
        if (value != null) {
            properties.setProperty(family + propertyPostfix, value);
        }
    }


    public String getServer() {
        return properties.getProperty(family + ".server");
    }

    public String getUrl() {
        return properties.getProperty(family + ".url");
    }

    public String getApiKey() {

        String environmentVariable =
                switch (family) {
                    case "chatgpt" -> "OPENAI_API_KEY";
                    case "anthropic" -> "ANTHROPIC_API_KEY";
                    default -> throw new RuntimeException(String.format("Cannot get api key for '%s', because it is an unknown family.", getFamily()));
                };

        // read the api key from the environment
        String apiKey = System.getenv(environmentVariable);
        return Objects.requireNonNullElse(apiKey, "");
    }

    public String getFamily() {
        return family;
    }

    public String getModel() {
        return properties.getProperty(family + ".model");
    }

    public int getMaxTokens() {
        return Integer.parseInt(properties.getProperty(family + ".maxTokens", "600"));
    }

    public int getTimeout() {
        return Integer.parseInt(properties.getProperty(family + ".timeout", "30"));
    }

    public int getRetries() {
        return Integer.parseInt(properties.getProperty("retries", "5"));
    }

    public String getPrompt() {
        return properties.getProperty(family + ".prompt");
    }

    public String getTestFile() {
        return argumentContainer.getTestFile();
    }

    public String getWorkingDirectory() {
        return argumentContainer.getWorkingDirectory();
    }
}
