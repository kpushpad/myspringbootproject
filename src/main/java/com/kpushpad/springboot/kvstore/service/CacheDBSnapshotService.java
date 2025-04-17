package com.kpushpad.springboot.kvstore.service;

import com.kpushpad.springboot.kvstore.model.ValueWithTTL;
import com.kpushpad.springboot.kvstore.serdser.SerialzDSer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

@Service
public class CacheDBSnapshotService<K, V> {

    private final SerialzDSer serialzDSer;
    private final String filePath;

    @Autowired
    public CacheDBSnapshotService(SerialzDSer serialzDSer,@Value("${kvstore.snapshot.file.path}") String filePath) {
        this.serialzDSer = serialzDSer;
        this.filePath = filePath;
    }

    public void saveSnapShot(Map<String, ValueWithTTL<String>> map) throws Exception {
        serialzDSer.serialize(filePath, map);
    }

    public Map<String, ValueWithTTL<String>> readSnapShot() throws Exception {
        return (Map<String, ValueWithTTL<String>>) serialzDSer.dSerialize(filePath);
    }

    public boolean snapShotFileExist() {
        File f = new File(filePath);
        return f.exists();
    }

    public String getFilePath() {
        return filePath;
    }
}
