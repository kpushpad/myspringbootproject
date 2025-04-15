package com.kpushpad.springboot.kvstore.common;

import com.kpushpad.springboot.kvstore.service.CacheAofCmdUtil;
import com.kpushpad.springboot.kvstore.service.CacheCleanUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SchedulerService {
    private final ScheduledExecutorService executorService;
    private final CacheCleanUpService cacheCleanUpService;
    private final FileService fileService;
    private final CacheAofCmdUtil cacheAofCmdUtil;


    @Autowired
    public SchedulerService(ScheduledExecutorService executorService, CacheCleanUpService cacheCleanUpService, FileService fileService, CacheAofCmdUtil cacheAofCmdUtil) {
        this.executorService = executorService;
        this.cacheCleanUpService = cacheCleanUpService;
        this.fileService = fileService;
        this.cacheAofCmdUtil = cacheAofCmdUtil;
        // Schedule task at fixed delay
        executorService.scheduleWithFixedDelay(this::cleanUpExpiredValue, 0, 10, TimeUnit.SECONDS);
        executorService.scheduleWithFixedDelay(this::flushAofFile, 0, 5, TimeUnit.SECONDS);
    }


    private void cleanUpExpiredValue() {
        System.out.println("Running cleanup Thread :" + System.currentTimeMillis());
        cacheCleanUpService.cleanUpExpiredCacheEntry();
    }

    private void flushAofFile()  {
        try {
            System.out.println("Running flush Thread :" + cacheAofCmdUtil.getAofFilePath());
            fileService.flushToDisk(cacheAofCmdUtil.getAofFilePath());
        } catch (Exception e) {
            System.out.println("Exp occurred in " + e.getStackTrace());
        }

    }
}
