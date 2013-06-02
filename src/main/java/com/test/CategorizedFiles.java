package com.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The heart of preprocessing list of files. Acquires the file, analyzes its length and split it across Queues per each
 * processing Thread.
 *
 * User: lanqu
 * Date: 01.06.13
 */
public class CategorizedFiles {

    private final List[] categories;
    private final long[] sizes;
    private final long maxSize;
    private final int overlap;

    /**
     * Constructs our utility class
     * @param numOfThreads our goal of Threads to use
     * @param overlap a tricky number, makes sense only in multithreaded case. Represents number of bytes that chunks
     *                overlaps on each other to not to miss the pattern if its start and end is in different chunks.
     */
    public CategorizedFiles(int numOfThreads, int overlap) {
        categories = new List[numOfThreads];
        sizes = new long[numOfThreads];
        for (int i = 0; i < numOfThreads; i++) {
            categories[i] = new ArrayList();
        }

        this.maxSize = calculateMaxSizeOfChunk(numOfThreads);
        this.overlap = overlap;
    }

    /**
     * Take only 75% of available memory for a Buffer for File per Thread
     * @param numOfThreads
     * @return
     */
    private long calculateMaxSizeOfChunk(int numOfThreads) {
        long maxMemory = Runtime.getRuntime().maxMemory();

        long memPerThread = maxMemory / numOfThreads;
        long chunkSize = memPerThread / 100 * 75;

        return chunkSize;
    }

    /**
     * Decides whether split file or not. Places its chunks in Queues for further processing
     * @param file
     * @throws IOException
     */
    public void addFile(File file) throws IOException {
        long offset = 0;
        long toAdd = maxSize <= file.length() ? maxSize : file.length();

        do {
            addFileWrapper(file, offset - overlap, toAdd + 2 * overlap);
            offset += toAdd;
            toAdd = offset + maxSize <= file.length() ? maxSize : file.length() - offset;
        } while (toAdd > 0);
    }

    /**
     * Just creates FileWrapper and adds it.
     * @param file
     * @param offset
     * @param length
     * @throws IOException
     */
    private void addFileWrapper(File file, long offset, long length) throws IOException {
        int pos = findSmallest();

        List list = categories[pos];
        list.add(new FileWrapper(file, offset, length));

        sizes[pos] = sizes[pos] + length;
    }

    /**
     * Utility method to find the smallest loaded bucket (queue)
     * @return
     */
    private int findSmallest() {
        int pos = 0;
        long size = sizes[0];

        for (int i = 0; i < sizes.length; i++) {
            if (sizes[i] < size) {
                pos = i;
                size = sizes[i];
            }
        }

        return pos;
    }

    public List[] getCategories() {
        return this.categories;
    }

}
