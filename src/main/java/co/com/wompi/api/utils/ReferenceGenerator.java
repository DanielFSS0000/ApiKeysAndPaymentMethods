package co.com.wompi.api.utils;

import java.util.Random;

public class ReferenceGenerator {

    private static final Random RANDOM = new Random();

    private ReferenceGenerator() {
        throw new IllegalStateException("Utility class");
    }

    public static String randomReference() {
        int randomNum = RANDOM.nextInt(1000000);
        return String.format("reference%06d", randomNum);
    }
}