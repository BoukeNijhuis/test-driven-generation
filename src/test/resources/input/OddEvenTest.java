package org.example.oddeven;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OddEvenTest {

    @Test
    public void test() {

        OddEven oddEven = new OddEven();
        assertTrue(oddEven.isEven(0));
        assertTrue(oddEven.isEven(2));
        assertTrue(oddEven.isEven(4));
        assertTrue(oddEven.isEven(6));
        assertTrue(oddEven.isEven(8));

        assertFalse(oddEven.isEven(1));
        assertFalse(oddEven.isEven(3));
        assertFalse(oddEven.isEven(5));
        assertFalse(oddEven.isEven(7));
        assertFalse(oddEven.isEven(9));
    }
}