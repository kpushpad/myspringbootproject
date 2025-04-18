package com.kpushpad.springboot.kvstore.common;

import com.kpushpad.springboot.kvstore.service.CacheAOFLogService;
import com.kpushpad.springboot.kvstore.service.CacheCleanExpKeysService;
import com.kpushpad.springboot.kvstore.service.CacheDBSnapshotService;
import com.kpushpad.springboot.kvstore.service.KvStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class CommonFacadeServ {
    private final CacheCleanExpKeysService cacheCleanExpKeysService;
    private final FileService fileService;
    private final CacheAOFLogService cacheAOFLogService;
    private final KvStoreService kvStoreService;
    private final CacheDBSnapshotService cacheDBSnapshotService;

    public CommonFacadeServ(CacheCleanExpKeysService cacheCleanExpKeysService, FileService fileService,
                            CacheAOFLogService cacheAOFLogService, KvStoreService kvStoreService, CacheDBSnapshotService cacheDBSnapshotService) {
        this.cacheCleanExpKeysService = cacheCleanExpKeysService;
        this.fileService = fileService;
        this.cacheAOFLogService = cacheAOFLogService;
        this.kvStoreService = kvStoreService;
        this.cacheDBSnapshotService = cacheDBSnapshotService;
    }

    public void cleanExpKeysAndTakeSnapshotWithRotation() throws Exception {
        Integer totalKeys = getTotalKeys();
        if (totalKeys > 0) {
            cacheCleanExpKeysService.cleanUpExpiredCacheEntry();
        }
        cacheAOFLogService.rotateAofFile();
        if (totalKeys > 0) {
            log.debug("********Taking snapshot of total keys {}***********", totalKeys);
            cacheDBSnapshotService.saveSnapShot(kvStoreService.getKeyMap());
        }
    }

    public void flushAofBackupFileContent() throws IOException {
        if (getTotalKeys() > 0) {
            fileService.flushToDisk(cacheAOFLogService.getAofFilePath());
        }
    }

    public Integer getTotalKeys() {
        return kvStoreService.getSize();
    }

}
