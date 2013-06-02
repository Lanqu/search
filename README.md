Search - Test for Deutche Bank
======

 Class Search is a tool for searching directories for files and analyzing them for a particular patter.
 To provide fast search it uses multithreading of Java 1.4.2. In common algorithm works in such way:
 <ol>
     <li>Traverse directories and find all files for processing</li>
     <li>Calculate the amount of memory for efficient use per each Thread</li>
     <li>Split large files into chunks based on previous calculations of available Heap</li>
     <li>Try to sort chunks of files between all queues per Thread and reach near the same amount of bytes to process
     in each Thread</li>
     <li>Send Queues each to its Thread to process</li>
     <li>Use InputStream capabilities and calculation of efficient buffers to load only a bit information at a time
     to not cause the OutOfMemoryException</li>
     <li>Mark found information about files to reduce time of processing other chunks of large file in other
     streams</li>
 </ol>

 To build ready-to-run jar use:

 <code>mvn clean package</code>

 Then you can execute ready-to-fun jar by:

 <code>java -jar target/search-1.0-SNAPSHOT-jar-with-dependencies.jar QUERY NUM_OF_THREADS [directory]</code>

 Here are allowed options:

 <ul>
     <li>QUERY - required parameter. Can be represented by String like "ABC123" or by unicode byte sequence like
     "\u0041\u0042\u0043\u0031\u0032\u0033" (btw, program uses UTF-8 to convert String into bytes)</li>
     <li>NUM_OF_THREADS - required parameter. You can use integer positive numbers here.</li>
     <li>[directory] - not required parameter. Defaults to current directory.</li>
 </ul>
 
 -------
 
 Interesting thing is about pattern matcher. I don't want user to pay the feature he dont use, so I didn't implement RegEx matcher.
 As there was no any desires about RegEx matching, so I've implemented the easiest - just search the substring in any file you pass.
 But for regEx mather I could implement my own Charsequence based on InputStream
 
 Another interesting fact is about improvements. For example, we can use something other than just Collections.contains() to check if 
 File is already found to contain our pattern. I came to solution like signaling to Analyzer Thread about stopping processing current 
 chunk, but that and many other implementations are already made in JDK greater than 1.4.2. Like using regEx for to match the Stream.
 
 -------
 
 Due to task, that says "output matching files", I decided not to continue search into the file if it alreade matched, so the results
 of time executions can vary on large files and using different amount of Threads. But in average - more Threads - the faster results appear.
  
 ------
 
 Some statistics on processing a directory with 9Gb file that matches the pattern. The match actually is located at the very end of the file.
 Also there are other smaller files without patter.
 
 Size, mb: 9272
 1 Thread -Xmx64: 66005, 65353, 65724 |No Xmx: 60619, 61961, 61274
 2 Thread -Xmx64: 43565, 43364, 43136 |No Xmx: 40621, 41396, 41401
 10 Threads: 45028, 44113, 43866 |No Xmx: 38020, 38563, 39180
 30 Threads: 50529, 49326, 49665 | No Xmx: 39019, 39067, 39845
 50 Threads:  47714, 47223, 47079 | No Xmx: 38327, 37859, 38872
 100 Threads: 49905, 49077, 48756 | No Xmx: 39880, 41405, 37349
 
 As we can see - number of threads and available Heap influence execution time. But due to some interference of size of files and number of Threads
 we can see some waves. But in commmon - more memory and more Threads - the less execution time.