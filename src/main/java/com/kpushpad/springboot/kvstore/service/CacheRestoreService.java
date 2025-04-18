package com.kpushpad.springboot.kvstore.service;

import com.kpushpad.springboot.kvstore.common.FileService;
import com.kpushpad.springboot.kvstore.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@Component
public class CacheRestoreService {
    private final CacheAOFLogService cacheAOFLogService;
    private final FileService fileService;
    private final KvStoreBusinessServ kvStoreBusinessServ;
    private final CacheDBSnapshotService cacheDBSnapshotService;


    public boolean isApplicationReadToUse() {
        return isApplicationReadToUse;
    }

    private boolean isApplicationReadToUse = false;

    @Autowired
    public CacheRestoreService(CacheAOFLogService cacheAOFLogService, FileService fileService,
                               KvStoreBusinessServ kvStoreBusinessServ,
                               CacheDBSnapshotService cacheDBSnapshotService) {
        this.cacheAOFLogService = cacheAOFLogService;
        this.fileService = fileService;
        this.kvStoreBusinessServ = kvStoreBusinessServ;
        this.cacheDBSnapshotService = cacheDBSnapshotService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void restore() throws Exception {
        log.debug("Started restoration....");
        // Read Snapshot file
        if (cacheDBSnapshotService.snapShotFileExist()) {
            Map map = cacheDBSnapshotService.readSnapShot();
            Integer count = kvStoreBusinessServ.fillData(map);
            log.debug("Loaded {} keys from Snapshot DB files", count);
        }

        String aofFilePath = cacheAOFLogService.getAofFilePath();
        long currentTime = System.currentTimeMillis();
        try {
            if (cacheAOFLogService.logFileExit()) {
                fileService.openForReading(aofFilePath);
                String line = fileService.readLine(aofFilePath);
                while (line != null) {
                    Pair<Integer, Map<String, String>> pair = cacheAOFLogService.parseCommand(line);
                    if (pair.getFirst().equals(CommonConstant.PUT)) {
                        long expiryTime = Long.parseLong(pair.getSecond().get(CommonConstant.TTL));
                        if (expiryTime > currentTime) {
                            kvStoreBusinessServ.put(pair.getSecond().get(CommonConstant.KEY),
                                    pair.getSecond().get(CommonConstant.VALUE),
                                    expiryTime);
                        } else if (pair.getFirst().equals(CommonConstant.DEL)) {
                            kvStoreBusinessServ.delete(pair.getSecond().get(CommonConstant.KEY));
                        }
                    }
                    line = fileService.readLine(aofFilePath);
                }
                fileService.closeReader(aofFilePath);
                fileService.moveToBackupFile(aofFilePath, cacheAOFLogService.getBackUpFileName(aofFilePath));
                fileService.openForWriting(cacheAOFLogService.getAofFilePath(), true);
            }
        } catch(IOException e){
            log.error("Error found during restoration{}", Arrays.toString(e.getStackTrace()));
            fileService.closeReader(aofFilePath);
        }
        if (!fileService.fileIsoOpenForWriting(cacheAOFLogService.getAofFilePath())) {
            fileService.openForWriting(cacheAOFLogService.getAofFilePath(), true);
        }
        if (!fileService.fileIsoOpenForWriting(cacheDBSnapshotService.getFilePath())) {
            fileService.openForWriting(cacheDBSnapshotService.getFilePath(), true);
        }
        isApplicationReadToUse = true;
        log.debug("Completed restoration....");
    }
}
