package nl.boukenijhuis.dto;

import nl.boukenijhuis.ClassNameNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeContainerTest {

    @Test
    void testFileNameExtractionHappyFlow() throws ClassNameNotFoundException {
        test("class HappyFlow{}", "HappyFlow.java");
        test("   class HappyFlow{}", "HappyFlow.java");
        test("public class HappyFlow{}", "HappyFlow.java");
        test(" public class HappyFlow{}", "HappyFlow.java");
        test("public class HappyFlow    {}", "HappyFlow.java");
        test("public     class HappyFlow{}", "HappyFlow.java");
        test("public class    HappyFlow{}", "HappyFlow.java");
    }

    @Test
    void testFileNameExtractionUnhappyFlow() {
        // TODO: implement
    }

    private void test(String content, String expectedFileName) throws ClassNameNotFoundException {
        CodeContainer co = new CodeContainer(content);
        assertEquals(expectedFileName, co.getFileName());
    }

}