package org.example.codecontainer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeContainerTest {

    @Test
    void fileNameExtraction() throws RuntimeException {
        testFileNameExtraction("class HappyFlow{}", "HappyFlow.java", "should be able to handle the simplest case");
        testFileNameExtraction("   class HappyFlow{}", "HappyFlow.java", "should be able to handle spaces before the class keyword");
        testFileNameExtraction("public class HappyFlow{}", "HappyFlow.java", "should be able to handle the public keyword before the class keyword");
        testFileNameExtraction(" public class HappyFlow{}", "HappyFlow.java", "should be able to handle spaces before the public keyword");
        testFileNameExtraction("public class HappyFlow    {}", "HappyFlow.java", "should be able to handle spaces after the class name");
        testFileNameExtraction("public     class HappyFlow{}", "HappyFlow.java", "should be able to handle spaces between the public and class keyword");
        testFileNameExtraction("public class    HappyFlow{}", "HappyFlow.java", "should be able to handle spaces between the class keyword and the class name");
    }

    private void testFileNameExtraction(String content, String expectedFileName, String message) throws RuntimeException {
        CodeContainer co = new CodeContainer(content);
        assertEquals(expectedFileName, co.getFileName(), message);
    }

    @Test
    void packageNameExtraction() throws RuntimeException {
        testPackageNameExtraction("package happyflow; class C{}", "happyflow", "should be be able to handle the simplest case");
        testPackageNameExtraction("   package happyflow; class C{}", "happyflow", "should be able to handle spaces before the package keyword");
        testPackageNameExtraction("package    happyflow; class C{}", "happyflow", "should be able to handle spaces between the package keyword and the package name");
        testPackageNameExtraction("package nl.happyflow; class C{}", "nl.happyflow", "should be able to handle package names consisting of multiple folders");
        testPackageNameExtraction("package happyflow   ; class C{}", "happyflow", "should be able to handle spaces between the package name and the semicolon");
        testPackageNameExtraction("class C{}", "", "should be able to handle the case with no package");
    }

    private void testPackageNameExtraction(String content, String expectedPackageName, String message) {
        CodeContainer co = new CodeContainer(content);
        assertEquals(expectedPackageName, co.getPackageName(), message);
    }

}