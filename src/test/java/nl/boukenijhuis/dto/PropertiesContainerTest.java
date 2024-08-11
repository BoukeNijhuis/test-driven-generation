package nl.boukenijhuis.dto;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertiesContainerTest {

    @Test
    public void expectFamilyFromPropertiesWhenThereIsNoFamilyInArguments() {
        String[] args = new String[]{};
        PropertiesContainer propertiesContainer = new PropertiesContainer(args);

        // no override, so expect the default value
        assertEquals("ollama", propertiesContainer.getFamily());
    }

    @Test
    public void expectFamilyFromArgumentsWhenThereIsFamilyInArguments() {
        String[] args = new String[]{"--family", "chatgpt"};
        PropertiesContainer propertiesContainer = new PropertiesContainer(args);

        // override, so expect the value from the arguments
        assertEquals("chatgpt", propertiesContainer.getFamily());
    }

    @Test
    public void testAllArgumentsExceptFamily() {
        // the fromProperties value comess from the test/resources/test-driven-generation.properties file
        testArgument("--server", "localhost:9999", "http://localhost:11434", PropertiesContainer::getServer);
        testArgument("--url", "/test", "/api/generate", PropertiesContainer::getUrl);
        testArgument("--model", "topmodel", "llama3.1", PropertiesContainer::getModel);
        testArgument("--max-tokens", "123", "600", propertiesContainer -> propertiesContainer.getMaxTokens() + "");
        testArgument("--timeout", "50", "32", propertiesContainer -> propertiesContainer.getTimeout() + "");
        testArgument("--prompt", "abc", null, PropertiesContainer::getPrompt);
    }

    public void testArgument(String flag, String value, String fromProperties, Function<PropertiesContainer, String> function) {
        String[] args = new String[]{flag, value};
        PropertiesContainer propertiesContainer = new PropertiesContainer(args);
        // override, so expect the value from the arguments
        assertEquals(value, function.apply(propertiesContainer));

        // replace arguments & properties container
        args = new String[]{};
        propertiesContainer = new PropertiesContainer(args);
        // no override, so expect the default value
        assertEquals(fromProperties, function.apply(propertiesContainer));
    }

}