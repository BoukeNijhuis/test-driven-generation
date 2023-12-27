package nl.boukenijhuis.dto;

public record FileNameContent(String fileName, String content, int attempts) {

    public FileNameContent(String fileName, String content){
        this(fileName, content, 1);
    }
}
