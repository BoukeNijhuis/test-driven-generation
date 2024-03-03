package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

class PrimeNumberGeneratorTestWithMultipleLevelPackage {

    @Test
    public void test() {

        PrimeNumberGeneratorWithMultipleLevelPackage primeNumberGenerator = new PrimeNumberGeneratorWithMultipleLevelPackage();
        assertEquals(List.of(2, 3, 5, 7), primeNumberGenerator.generate(10));
    }
}