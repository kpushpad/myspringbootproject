package com.kpushpad.springboot.kvstore.service;

import com.kpushpad.springboot.kvstore.common.FileService;
import com.kpushpad.springboot.kvstore.constant.CommonConstant;
import com.kpushpad.springboot.kvstore.model.CacheEntry;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class CacheAOFLogService {
    private static final String PUT = "PUT";
    private static final String DEL = "DEL";

    private final FileService fileService;
    private final String filePath;
    private String baseFileName = null;

    @Autowired
    public CacheAOFLogService(FileService fileService, @Value("${kvstore.aof.file.path}") String filePath) {
        this.fileService = fileService;
        this.filePath = filePath;
        this.baseFileName = filePath.contains(".")
                ? filePath.substring(0, filePath.lastIndexOf('.')) : filePath;
    }

    public String getPutCmd(String key, String value, String ttl) {
        return "PUT " + key + " " + value + " " + ttl;
    }

    public String getDelCmd(String key) {
        return "DEL " + key;
    }

    public String getAofFilePath() {
        return filePath;
    }

    public String getBackUpFileName(String filename) {
        return baseFileName + "_" + ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) + ".bk";
    }

    public Pair<Integer, Map<String, String>> parseCommand(String cmd) {
        Map<String, String> map = new HashMap<>();
        if (cmd.contains(PUT)) {
            String[] tokens = cmd.trim().split(" +");
            map.put(CommonConstant.KEY, tokens[1]);
            map.put(CommonConstant.VALUE, tokens[2]);
            map.put(CommonConstant.TTL, tokens[3]);
            return Pair.of(CommonConstant.PUT, map);
        } else if (cmd.contains(DEL)) {
            String[] tokens = cmd.trim().split(" +");
            map.put(CommonConstant.KEY, tokens[1]);
            return Pair.of(CommonConstant.DEL, map);
        }
        return null;
    }

    public boolean logFileExit() {
        File file = new File(filePath);
        return file.exists() && file.length() > 0;
    }

    public void rotateAofFile() throws IOException {
        if (fileService.getFileSize(filePath) > 0) {
            fileService.flushToDisk(filePath);
            fileService.closeWriter(filePath);
            fileService.moveToBackupFile(filePath, getBackUpFileName(filePath));
            fileService.openForWriting(filePath, true);
        }
    }

    public <K, V> void recordPutCommand(CacheEntry<K,V> entry) throws IOException {
        fileService.writeLine(filePath, getPutCmd(entry.getKey().toString(), entry.getValue().getKey().toString(),
                entry.getValue().getExpiryTimeInMs().toString()));
    }

    public <K, V> void recordDelCommand(CacheEntry<K,V> entry) throws IOException  {
        fileService.writeLine(filePath,getDelCmd(entry.getValue().toString()));
    }

    @PreDestroy
    public void onShutdown() throws IOException {
        log.debug("Application is shutting down..... . Flushing AOF file context");
        fileService.flushToDisk(filePath);
        fileService.closeWriter(filePath);
    }
}
