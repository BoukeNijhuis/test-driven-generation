package nl.boukenijhuis.dto;

public class ArgumentContainer {

    String testFile;
    String workingDirectory;
    String family;
    String model;
    // TODO: add timeout

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
                case "--family": {
                    family = args[++i];
                    break;
                }
                case "--model": {
                    model = args[++i];
                    break;
                }
            }
        }

        // check the variables
        if (testFile == null) {
            throw new RuntimeException("No JUnit 5 test file provided as command-line argument.");
        }
    }

    public String getTestFile() {
        return testFile;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public String getFamily() {
        return family;
    }

    public String getModel() {
        return model;
    }
}
