package com.test;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: lanqu
 * Date: 02.06.13
 */
public class SearchTest extends TestCase {

    private byte[] query;

    public void setUp() throws UnsupportedEncodingException {
        query = "ABC123".getBytes("UTF-8");
    }

    public void testSearchTenThreadsFasterOneThread() throws InterruptedException, SearchException, IOException {
        Collection list = new ArrayList();
        Search search = new Search(query, "src/test/resources", 1, list);
        long time1 = search.doSearch();

        list = new ArrayList();
        search = new Search(query, "src/test/resources", 5, list);
        long time10 = search.doSearch();

        assertTrue(time1 >= time10);
    }

    public void testSearchOneThread() throws InterruptedException, SearchException, IOException {
        Collection list = new ArrayList();
        Search search = new Search(query, "src/test/resources", 1, list);
        search.doSearch();
        assertTrue(list.size() == 3);
    }

    public void testSearchTenThreads() throws InterruptedException, SearchException, IOException {
        Collection list = new ArrayList();
        Search search = new Search(query, "src/test/resources", 5, list);
        search.doSearch();
        assertTrue(list.size() == 3);
    }

}
