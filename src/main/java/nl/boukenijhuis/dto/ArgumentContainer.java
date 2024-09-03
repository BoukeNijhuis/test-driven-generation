package nl.boukenijhuis.dto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArgumentContainer {

    String testFile;
    String workingDirectory;
    String server;
    String url;
    // TODO make this an enum?
    String family;
    String model;
    String maxTokens;
    String timeout;
    String prompt;

    private static final Logger LOG = LogManager.getLogger(ArgumentContainer.class);

    public ArgumentContainer(String[] args) {

        // special case with only one argument
        if (args.length == 1) {
            testFile = args[0];
            return;
        }

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--test-file": {
                    testFile = args[++i];
                    break;
                }
                case "--working-directory": {
                    workingDirectory = args[++i];
                    break;
                }
                case "--server": {
                    server = args[++i];
                    break;
                }
                case "--url": {
                    url = args[++i];
                    break;
                }
                case "--family": {
                    family = args[++i];
                    break;
                }
                case "--model": {
                    model = args[++i];
                    break;
                }
                case "--max-tokens": {
                    maxTokens = args[++i];
                    break;
                }
                case "--timeout": {
                    timeout = args[++i];
                    break;
                }
                case "--prompt": {
                    prompt = args[++i];
                    break;
                }
                default: {
                    LOG.warn("Unknown flag {} with value {} detected!", args[i], args[++i]);
                }
            }
        }
    }

    public String getTestFile() {
        return testFile;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public String getServer() {
        return server;
    }

    public String getUrl() {
        return url;
    }

    public String getFamily() {
        return family;
    }

    public String getModel() {
        return model;
    }

    public String getMaxTokens() {
        return maxTokens;
    }

    public String getTimeout() {
        return timeout;
    }

    public String getPrompt() {
        return prompt;
    }
}
