package org.example.primenumber;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

class PrimeNumberGeneratorWithExceptionTest {

    @Test
    public void happyFlow() {
        PrimeNumberGenerator primeNumberGenerator = new PrimeNumberGenerator();
        assertEquals(List.of(2, 3, 5, 7), primeNumberGenerator.generate(10));
    }

    @Test
    public void unhappyFlow() {
        assertThrows(RuntimeException.class, () -> {
            PrimeNumberGenerator primeNumberGenerator = new PrimeNumberGenerator();
            primeNumberGenerator.generate(-1);
        });
    }
}