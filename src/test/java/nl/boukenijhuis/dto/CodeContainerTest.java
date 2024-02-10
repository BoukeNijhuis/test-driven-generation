package nl.boukenijhuis.dto;

import nl.boukenijhuis.ClassNameNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeContainerTest {

    @Test
    void fileNameExtractionHappyFlow() throws ClassNameNotFoundException {
        testFileNameExtraction("class HappyFlow{}", "HappyFlow.java");
        testFileNameExtraction("   class HappyFlow{}", "HappyFlow.java");
        testFileNameExtraction("public class HappyFlow{}", "HappyFlow.java");
        testFileNameExtraction(" public class HappyFlow{}", "HappyFlow.java");
        testFileNameExtraction("public class HappyFlow    {}", "HappyFlow.java");
        testFileNameExtraction("public     class HappyFlow{}", "HappyFlow.java");
        testFileNameExtraction("public class    HappyFlow{}", "HappyFlow.java");
    }

    @Test
    void fileNameExtractionUnhappyFlow() {
        // TODO: implement
    }

    private void testFileNameExtraction(String content, String expectedFileName) throws ClassNameNotFoundException {
        CodeContainer co = new CodeContainer(content);
        assertEquals(expectedFileName, co.getFileName());
    }

    @Test
    void packageNameExtractionHappyFlow() throws ClassNameNotFoundException {
        testPackageNameExtraction("package happyflow; class C{}", "happyflow");
        testPackageNameExtraction("   package happyflow; class C{}", "happyflow");
        testPackageNameExtraction("package    happyflow; class C{}", "happyflow");
        testPackageNameExtraction("package nl.happyflow; class C{}", "nl.happyflow");
        testPackageNameExtraction("package happyflow   ; class C{}", "happyflow");
        testPackageNameExtraction("class C{}", "");
    }

    @Test
    void packageNameExtractionUnhappyFlow() {
        // TODO: implement
    }

    private void testPackageNameExtraction(String content, String expectedPackageName) throws ClassNameNotFoundException {
        CodeContainer co = new CodeContainer(content);
        assertEquals(expectedPackageName, co.getPackageName());
    }

}