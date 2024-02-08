package nl.boukenijhuis.dto;

public record CompilationContainer(boolean compilationSuccessful, String errorMessage) {

    public CompilationContainer() {
        this(true, null);
    }

    public CompilationContainer(String errorMessage) {
        this(false, errorMessage);
    }
}
