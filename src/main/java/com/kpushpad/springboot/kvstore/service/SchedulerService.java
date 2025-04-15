package com.kpushpad.springboot.kvstore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SchedulerService {
    private final ScheduledExecutorService executorService;
    private final CacheCleanUpService cacheCleanUpService;


    @Autowired
    public SchedulerService(ScheduledExecutorService executorService, CacheCleanUpService cacheCleanUpService) {
        this.executorService = executorService;
        this.cacheCleanUpService = cacheCleanUpService;
        // Schedule task at fixed delay
        executorService.scheduleWithFixedDelay(this::runTask, 0, 10, TimeUnit.SECONDS);
    }

    private void runTask() {
        System.out.println("Running schedule Thread :" + System.currentTimeMillis());
        cacheCleanUpService.cleanUpExpiredCacheEntry();
    }
}
