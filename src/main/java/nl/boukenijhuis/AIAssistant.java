package nl.boukenijhuis;

import nl.boukenijhuis.dto.FileNameContent;

import java.io.IOException;
import java.nio.file.Path;

public interface AIAssistant {
    FileNameContent call(Path testFile) throws IOException, InterruptedException;
}
