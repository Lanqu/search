
package com.test;

/**
 * Just common exception to be thrown if any validation fails.
 *
 * User: lanqu
 * Date: 01.06.13
 */
public class SearchException extends Exception {
    public SearchException(String message) {
        super(message);
    }
}
