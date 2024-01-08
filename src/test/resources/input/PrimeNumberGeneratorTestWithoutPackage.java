import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

class PrimeNumberGeneratorTestWithoutPackage {

    @Test
    public void test() {

        PrimeNumberGeneratorWithoutPackage primeNumberGenerator = new PrimeNumberGeneratorWithoutPackage();
        assertEquals(List.of(2, 3, 5, 7), primeNumberGenerator.generate(10));
    }
}