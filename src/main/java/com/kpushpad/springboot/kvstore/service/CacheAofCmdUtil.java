package com.kpushpad.springboot.kvstore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CacheAofCmdUtil {

    private final String filePath;

    @Autowired
    public CacheAofCmdUtil(@Value("${kvstore.aof.file.path}")String filePath) {
        this.filePath = filePath;
    }

    public String getPutCmd(String key, String value, String ttl) {
        return "PUT" + " key"  + " value"  + " ttl";
    }

    public String getDelCmd(String key) {
        return "DEL" + " key";
    }

    public String getAofFilePath() {
        return filePath;
    }
}
