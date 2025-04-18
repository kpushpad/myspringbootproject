package com.kpushpad.springboot.kvstore.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@Slf4j
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

    public void moveToBackupFile(String filename,String newFileName) throws IOException {
        File orignalFile = new File(filename);
        File renamedFile = new File(newFileName);
        Files.move(orignalFile.toPath(), renamedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    // Flush file content to DISK.
    public void flushToDisk(String filename) throws IOException {
        BufferedWriter writer = openWriters.get(filename);
        writer.flush();
        FileOutputStream fos = flushWriters.get(filename);
        fos.getFD().sync();
    }

    public Long getFileSize(String filename) throws IOException {
        return Files.size(new File(filename).toPath());
    }

    public boolean fileIsoOpenForWriting(String filename) {
        return openWriters.get(filename) != null;
    }

}
