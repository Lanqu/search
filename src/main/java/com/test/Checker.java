package com.test;

/**
 * User: lanqu
 * Date: 30.05.13
 */
public class Checker {

    private final byte[] toCheck;
    private int i;
    boolean equals = false;

    public Checker(byte[] toCheck) {
        this.toCheck = toCheck;
    }

    public boolean appendAndCheck(byte b) {
        if (b == toCheck[i]) {
            i++;

            if (i == toCheck.length) {
                equals = true;
                i = 0;
            }
        } else {
            i = 0;
        }

        return equals;
    }
}
