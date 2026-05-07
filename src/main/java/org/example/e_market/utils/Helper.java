package org.example.e_market.utils;

import java.security.SecureRandom;


public class Helper {

    public static String generateNumericOtp(int length) {
        SecureRandom random = new SecureRandom();
        return String.format("%0" + length + "d", random.nextInt((int) Math.pow(10, length)));
    }


}
