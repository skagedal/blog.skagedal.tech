package com.codewaves.codehighlight.core;

import java.util.Arrays;

/**
 * @author Sayi
 */
public final class Regex {

    public static String either(String[] words) {
        String[] array = Arrays.stream(words).map(str -> "(" + str + ")").toArray(String[]::new);
        return String.join("|", array);
    }

}
