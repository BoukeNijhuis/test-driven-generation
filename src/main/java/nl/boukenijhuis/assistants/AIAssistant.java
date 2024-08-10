package nl.boukenijhuis.assistants;

import nl.boukenijhuis.dto.CodeContainer;
import nl.boukenijhuis.dto.PreviousRunContainer;
import nl.boukenijhuis.dto.PropertiesContainer;

import java.io.IOException;
import java.nio.file.Path;

public interface AIAssistant {

    CodeContainer call(Path testFile, PreviousRunContainer previousRunContainer) throws IOException, InterruptedException;

    PropertiesContainer getProperties();
}
