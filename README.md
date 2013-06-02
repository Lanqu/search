Search - Test for Deutche Bank
======

 Class Search is a tool for searching directories for files and analyzing them for a particular patter.
 To provide fast search it uses multithreading of Java 1.4.2. In commong algorithm works in such way:
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