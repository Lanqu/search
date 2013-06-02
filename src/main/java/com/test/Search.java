package com.test;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Class Search is a tool for searching directories for files and analyzing them for a particular patter.
 * To provide fast search it uses multithreading of Java 1.4.2. In commong algorithm works in such way:
 * <ol>
 *     <li>Traverse directories and find all files for processing</li>
 *     <li>Calculate the amount of memory for efficient use per each Thread</li>
 *     <li>Split large files into chunks based on previous calculations of available Heap</li>
 *     <li>Try to sort chunks of files between all queues per Thread and reach near the same amount of bytes to process
 *     in each Thread</li>
 *     <li>Send Queues each to its Thread to process</li>
 *     <li>Use InputStream capabilities and calculation of efficient buffers to load only a bit information at a time
 *     to not cause the OutOfMemoryException</li>
 *     <li>Mark found information about files to reduce time of processing other chunks of large file in other
 *     streams</li>
 * </ol>
 *
 * To build ready-to-run jar use:
 *
 * <code>mvn clean package</code>
 *
 * Then you can execute ready-to-fun jar by:
 *
 * <code>java -jar target/search-1.0-SNAPSHOT-jar-with-dependencies.jar QUERY NUM_OF_THREADS [directory]</code>
 *
 * Here are allowed options:
 *
 * <ul>
 *     <li>QUERY - required parameter. Can be represented by String like "ABC123" or by unicode byte sequence like
 *     "\u0041\u0042\u0043\u0031\u0032\u0033" (btw, program uses UTF-8 to convert String into bytes)</li>
 *     <li>NUM_OF_THREADS - required parameter. You can use integer positive numbers here.</li>
 *     <li>[directory] - not required parameter. Defaults to current directory.</li>
 * </ul>
 *
 *
 * @author lanqu
 * @version 1.0
 */
public class Search {

    private final byte[] query;
    private final String dirPath;
    private final int numOfThreads;
    private final Collection foundFiles;

    public static void main(String[] args) throws IOException, InterruptedException {

        byte[] query;

        String dirPath = ".";
        int numOfThreads = 1;

        try {
            String q = StringEscapeUtils.unescapeJava(args[0]);
            query = StringEscapeUtils.unescapeJava(q).getBytes("UTF-8");
            System.out.println("QUERY: " + q);
        } catch (IndexOutOfBoundsException e) {
            printUsage();
            return;
        }

        try {
            numOfThreads = Integer.parseInt(args[1]);

            if (numOfThreads < 1) {
                System.out.println("You can't use number of threads = " + numOfThreads);
                printUsage();
                return;
            }
        } catch (java.lang.NumberFormatException e) {
            System.out.println("Weird. Number of threads = " + args[1] + ". Really? Oh common.");
        }
        System.out.println("Using " + numOfThreads + " threads");

        try {
            dirPath = args[2];
        } catch (Throwable e) {
            System.out.println("Using current directory for search");
        }

        try {
            new Search(query, dirPath, numOfThreads, new LinkedList()).doSearch();
        } catch (SearchException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java -jar search-1.0-SNAPSHOT-jar-with-dependencies.jar QUERY NUM_OF_THREADS [directory]");
    }

    public Search(byte[] query, String dirPath, int numOfThreads, Collection foundFiles) {
        this.query = query;
        this.dirPath = dirPath;
        this.numOfThreads = numOfThreads;
        this.foundFiles = foundFiles;
    }

    /**
     * Do search based on input parameters
     * @return execution time
     * @throws IOException Shouldn't be thrown. But if any..
     * @throws InterruptedException The same. If it will be thrown - then no results of searching. No sense in exception
     * @throws SearchException is thrown if something that we must handle happened
     */
    public long doSearch() throws IOException, InterruptedException, SearchException {
        // Start timer
        long time = System.currentTimeMillis();

        File dir = new File(dirPath);

        List listOfFiles = new LinkedList();
        fillListWithFiles(dir, listOfFiles);
        foundFiles.addAll(analyze(listOfFiles));

        for (Iterator it = foundFiles.iterator(); it.hasNext(); ) {
            System.out.println(it.next());
        }

        long result = System.currentTimeMillis() - time;
        System.out.println("Execution, ms: " + Long.toString(result));
        System.out.println("Size, mb: " + Long.toString(sizeOfFiles(listOfFiles) / 1024 / 1024));

        return result;
    }

    /**
     * Run Threads and process all files to find the pattern.
     * Basically method calls to CategorizedFiles to split and sort file chunks across Queues.
     * 1 Queue per Thread.
     * @param listOfFiles
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    private Collection analyze(List listOfFiles) throws InterruptedException, IOException {

        List listRunningThreads = new LinkedList();

        // Using synchronized Set is all we need, no other synchronization
        Set foundFiles = Collections.synchronizedSet(new HashSet());

        CategorizedFiles cat = new CategorizedFiles(numOfThreads, query.length);

        // Split and Sort Files
        for (ListIterator it = listOfFiles.listIterator(); it.hasNext(); ) {
            final File file = (File) it.next();
            cat.addFile(file);
        }

        // Create all Threads to run
        for (int i = 0; i < cat.getCategories().length; i++) {
            Thread t = new Thread(new Analyzer(cat.getCategories()[i], query, foundFiles));
            listRunningThreads.add(t);
        }

        // Run
        for (Iterator it = listRunningThreads.iterator(); it.hasNext(); ) {
            ((Thread) it.next()).start();
        }

        // Collect cherry
        for (Iterator it = listRunningThreads.iterator(); it.hasNext(); ) {
            ((Thread) it.next()).join();
        }

        return foundFiles;
    }

    /**
     * Find all files in all subdirectories
     * @param dir
     * @param list
     * @throws SearchException
     */
    private void fillListWithFiles(File dir, List list) throws SearchException {
        File[] files = dir.listFiles();

        if (files == null) {
            throw new SearchException("Can't find directory " + dir.toString());
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];

            if (file.isFile()) {
                list.add(file);
            } else {
                fillListWithFiles(file, list);
            }
        }
    }

    /**
     * Size of all files found
     * @param listOfFiles
     * @return
     */
    private long sizeOfFiles(List listOfFiles) {
        long size = 0;
        for (Iterator it = listOfFiles.iterator(); it.hasNext(); ) {
            size += ((File) it.next()).length();
        }

        return size;
    }

}
