package com.test;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This class is the core of searching Threads. Each Analyzer knows about personal Queue (listOfCategorizedPieces)
 * with FileWrappers (chunks). Then it searches each chunk for pattern, and if it finds, then marks the file as
 * processed. Still not processed chunks of the same File will be skipped to save the time.
 *
 * User: lanqu
 * Date: 01.06.13
 */
public class Analyzer implements Runnable {

    private final List listOfCategorizedPieces;
    private final byte[] query;
    private final Collection foundFiles;

    public Analyzer(List listOfCategorizedPieces, byte[] query, Collection foundFiles) {
        this.query = query;
        this.foundFiles = foundFiles;
        this.listOfCategorizedPieces = listOfCategorizedPieces;
    }

    public void run() {
        outter:
        for (Iterator it = listOfCategorizedPieces.iterator(); it.hasNext(); ) {

            FileWrapper in = ((FileWrapper) it.next());

            if (foundFiles.contains(in.getFile())) {
                continue outter;
            }

            try {
                Checker checker = new Checker(query);

                /* Using buffer like 1/3 of chunk size.
                   Casting long to int, because I hardly believe that you can provide Heap more than 2^16 */
                byte[] b = new byte[((int) in.getLength() / 3)];

                long i = in.read(b);

                while (i != -1) {

                    for (int n = 0; n < i; n++) {
                        if (checker.appendAndCheck(b[n])) {

                            // synchronized collection
                            foundFiles.add(in.getFile());
                            continue outter;
                        }
                    }

                    i = in.read(b);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
