//package input;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

class PrimeNumberGeneratorTest {

    @Test
    public void test() {

        PrimeNumberGenerator primeNumberGenerator = new PrimeNumberGenerator();
        assertEquals(List.of(2, 3, 5, 7), primeNumberGenerator.generate(10));
    }
}