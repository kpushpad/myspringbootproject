package com.kpushpad.springboot.kvstore.common;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileService {

    private final Map<String, BufferedReader> openReaders = new HashMap<>();
    private final Map<String, BufferedWriter> openWriters = new HashMap<>();
    private final Map<String, FileOutputStream>  flushWriters = new HashMap<>();

    public void openForReading(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        openReaders.put(filename, reader);
    }

    public void openForWriting(String filename, boolean append) throws IOException {
        FileOutputStream fos = new FileOutputStream(filename, append);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
        openWriters.put(filename, writer);
        flushWriters.put(filename, fos);
    }
    // Read a line from an open file
    public String readLine(String filename) throws IOException {
        BufferedReader reader = openReaders.get(filename);
        if (reader == null) {
            throw new IOException("Reader not opened for file: " + filename);
        }
        return reader.readLine();
    }

    // Write a line to an open file
    public void writeLine(String filename, String line) throws IOException {
        BufferedWriter writer = openWriters.get(filename);
        if (writer == null) {
            throw new IOException("Writer not opened for file: " + filename);
        }
        writer.write(line);
        writer.newLine();
    }

    // Close reading stream
    public void closeReader(String filename) throws IOException {
        BufferedReader reader = openReaders.remove(filename);
        if (reader != null) {
            reader.close();
        }
    }

    // Close writing stream
    public void closeWriter(String filename) throws IOException {
        BufferedWriter writer = openWriters.remove(filename);
        if (writer != null) {
            writer.close();
        }
    }

    // Flush file content to DISK.
    public void flushToDisk(String filename) throws IOException {
        FileOutputStream fos = flushWriters.get(filename);
        fos.getFD().sync();
    }

}
