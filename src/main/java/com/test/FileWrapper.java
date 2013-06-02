package com.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * This class represents a wrapper on the File. It acts as a view for the File allowing to split one file onto several
 * chunks.
 *
 * User: lanqu
 * Date: 01.06.13
 */
public class FileWrapper {

    private final File file;
    private FileInputStream is;
    private final long offset;
    private long length;
    private long alreadyRead;

    /**
     * Rely on check if offset is positive
     * @param file
     * @param offset
     * @param length
     * @throws IOException
     */
    public FileWrapper(File file, long offset, long length) throws IOException {
        this.file = file;
        this.offset = offset < 0 ? 0 : offset;
        this.length = length;
        this.alreadyRead = 0;
    }

    /**
     * Method is a wrapper over Stream's read and provides the bounds of File. Only bounded area of a File can be read.
     *
     * @param b
     * @return actually read bytes
     * @throws IOException
     */
    public long read(byte[] b) throws IOException {
        if (is == null) {
            this.is = new FileInputStream(file);
            is.skip(offset);
        }

        if (alreadyRead >= length) {
            return -1;
        }

        long read = is.read(b);
        alreadyRead += read;

        if (alreadyRead > length) {
            return read - (alreadyRead - length);
        }

        return read;
    }

    public void close() throws IOException {
        is.close();
    }

    public File getFile() {
        return this.file;
    }

    public long getLength() {
        return length;
    }

}
