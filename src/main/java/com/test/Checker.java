package com.test;

/**
 * Pattern matcher
 *
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

    /**
     * Checker uses incomming bytes to match the pattern. It can match only straight patterns, not the RegEx expressions
     * @param b
     * @return
     */
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
