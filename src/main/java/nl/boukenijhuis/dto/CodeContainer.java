package nl.boukenijhuis.dto;

import nl.boukenijhuis.Utils;

public record CodeContainer(String fileName, String content, int attempts) {

    public CodeContainer(String fileName, String content) {
        this(fileName, content, 1);
    }

    public String getPackageName() {
        return Utils.getPackageName(content);
    }
}
