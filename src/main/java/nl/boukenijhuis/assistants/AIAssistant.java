package nl.boukenijhuis.assistants;

import nl.boukenijhuis.dto.CodeContainer;

import java.io.IOException;
import java.nio.file.Path;

public interface AIAssistant {
    CodeContainer call(Path testFile) throws IOException, InterruptedException;
}
