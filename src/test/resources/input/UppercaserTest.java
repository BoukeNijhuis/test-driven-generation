import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UppercaserTest {

    @Test
    public void testAAA() {
        Uppercaser upperCaser = new Uppercaser();
        assertEquals("AAA", upperCaser.upperCase("aaa"));
    }

    @Test
    public void testBBB() {
        Uppercaser upperCaser = new Uppercaser();
        assertEquals("BBB", upperCaser.upperCase("bbb"));
    }

    @Test
    public void testCCC() {
        Uppercaser upperCaser = new Uppercaser();
        assertEquals("CCC", upperCaser.upperCase("ccc"));
    }
}