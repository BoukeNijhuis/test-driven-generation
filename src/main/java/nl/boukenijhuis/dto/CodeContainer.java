package nl.boukenijhuis.dto;

public record CodeContainer(String fileName, String content, int attempts) {

    public CodeContainer(String fileName, String content){
        this(fileName, content, 1);
    }

    public String getPackageName() {
        String searchString = "package ";
        int packageStart = content.indexOf(searchString);
        String packageName = "";

        // there is a package
        if (packageStart >= 0) {
            int packageEnd = content.indexOf(";", packageStart);
            packageName = content.substring(packageStart + searchString.length(), packageEnd);
        }

        return packageName;

    }
}
