package nl.boukenijhuis.dto;

import nl.boukenijhuis.Generator;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class PropertiesContainer {

    protected Properties properties;
    private ArgumentContainer argumentContainer;
    private String family;

    // TODO check if everywhere the commandline overwrites the properties file
    // TODO write test to check the previous line
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

        // determine the family (default to Ollama)
        family = argumentContainer.getFamily();
        if (family == null) {
            family = "ollama";
        }

        // update the properties when a model is provided as argument
        String model = argumentContainer.getModel();
        if (model != null) {
            properties.setProperty(family + ".model", model);
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
        return Integer.parseInt(properties.getProperty(family + ".timeout", "5"));
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
