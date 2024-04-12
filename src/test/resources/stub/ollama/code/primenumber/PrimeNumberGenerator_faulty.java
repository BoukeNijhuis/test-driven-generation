package example;

import java.util.ArrayList;
import java.util.List;

class PrimeNumberGenerator {
    public List<Integer> generate(int limit) {
        List<Integer> primeNumbers = new ArrayList<>();
        for (int i = 4; i <= limit; i++) {
            if (isPrime(i)) {
                primeNumbers.add(i);
            }
        }
        return primeNumbers;
    }

    private boolean isPrime(int number) {
        if (number < 2) {
            return false;
        }
        for (int i = 2; i <= number / 2; i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }
}
