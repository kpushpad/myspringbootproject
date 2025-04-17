package com.kpushpad.springboot.kvstore.service;

import com.kpushpad.springboot.kvstore.common.FileService;
import com.kpushpad.springboot.kvstore.constant.CommonConstant;
import com.kpushpad.springboot.kvstore.model.ValueWithTTL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CacheRestoreService {
    private final CacheAofUtil cacheAofUtil;
    private final FileService fileService;
    private final KvStoreBusinessServ kvStoreBusinessServ;
    private final CacheDBSnapshotService cacheDBSnapshotService;


    public boolean isApplicationReadToUse() {
        return isApplicationReadToUse;
    }

    private boolean isApplicationReadToUse = false;

    @Autowired
    public CacheRestoreService(CacheAofUtil cacheAofUtil, FileService fileService,
                               KvStoreBusinessServ kvStoreBusinessServ,
                               CacheDBSnapshotService cacheDBSnapshotService) {
        this.cacheAofUtil = cacheAofUtil;
        this.fileService = fileService;
        this.kvStoreBusinessServ = kvStoreBusinessServ;
        this.cacheDBSnapshotService = cacheDBSnapshotService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void restore() throws Exception {
        System.out.println("Started restoration....");
        // Read Snapshot file
        if (cacheDBSnapshotService.snapShotFileExist()) {
            Map<String, ValueWithTTL<String>> map = cacheDBSnapshotService.readSnapShot();
            Integer count = kvStoreBusinessServ.fillData(map);
            System.out.println("Loaded " + count + " keys from Snapshot DB files");
        }

        String aofFilePath = cacheAofUtil.getAofFilePath();
        Long currentTime = System.currentTimeMillis();
        try {
            if (cacheAofUtil.logFileExit()) {
                fileService.openForReading(aofFilePath);
                String line = fileService.readLine(aofFilePath);
                while (line != null) {
                    Pair<Integer, Map<String, String>> pair = cacheAofUtil.parseCommand(line);
                    Long expiryTime = Long.parseLong(pair.getSecond().get(CommonConstant.TTL));
                    if (pair.getFirst().equals(CommonConstant.PUT)) {
                        if (expiryTime > currentTime) {
                            kvStoreBusinessServ.put(pair.getSecond().get(CommonConstant.KEY),
                                    pair.getSecond().get(CommonConstant.VALUE),
                                    expiryTime);
                        } else if (pair.getFirst().equals(CommonConstant.DEL)) {
                            kvStoreBusinessServ.delete(pair.getSecond().get(CommonConstant.KEY));
                        }
                    }
                    line = fileService.readLine(aofFilePath);
                    fileService.closeReader(aofFilePath);
                    fileService.moveToBackupFile(aofFilePath, cacheAofUtil.getBackUpFileName(aofFilePath));
                    fileService.openForWriting(cacheAofUtil.getAofFilePath(), true);
                }

            }
        } catch(IOException e){
            System.out.println("Error found during restoration" + e.getStackTrace());
        }
        if (!fileService.fileIsoOpenForWriting(cacheAofUtil.getAofFilePath())) {
            fileService.openForWriting(cacheAofUtil.getAofFilePath(), true);
        }
        if (!fileService.fileIsoOpenForWriting(cacheDBSnapshotService.getFilePath())) {
            fileService.openForWriting(cacheDBSnapshotService.getFilePath(), true);
        }
        isApplicationReadToUse = true;
        fileService.closeReader(aofFilePath);
        System.out.println("Completed restoration....");
    }
}
