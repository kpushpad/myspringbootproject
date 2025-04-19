package com.kpushpad.springboot.kvstore.common;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SchedulerService {
    private final ScheduledExecutorService executorService;
    private final CommonFacadeServ commonFacadeServ;
    private final Integer initialCleanupExpValTaskDelay;
    private final Integer cleanupExpValTaskDelay;
    private final Integer initialFlushValTaskDelay;
    private final Integer flushValTaskDelay;


    @Autowired
    public SchedulerService(ScheduledExecutorService executorService, CommonFacadeServ commonFacadeServ,
                            @Value("${kvstore.cleanup.expired.initial.delay}") Integer initialCleanupExpValTaskDelay,
                            @Value("${kvstore.cleanup.expired.delay}") Integer cleanupExpValTaskDelay,
                            @Value("${kvstore.flush.aof.initial.delay}") Integer initialFlushValTaskDelay,
                            @Value("${kvstore.flush.aof.delay}")Integer flushValTaskDelay) {
        this.executorService = executorService;
        this.commonFacadeServ = commonFacadeServ;
        this.initialCleanupExpValTaskDelay = initialCleanupExpValTaskDelay;
        this.cleanupExpValTaskDelay = cleanupExpValTaskDelay;
        this.initialFlushValTaskDelay = initialFlushValTaskDelay;
        this.flushValTaskDelay = flushValTaskDelay;
    }

    private void cleanUpExpiredValue() {
        try {
            commonFacadeServ.cleanExpKeysAndTakeSnapshotWithRotation();
        } catch(Exception e) {
            log.debug("Exp occurred in cleanUpExpiredValue{}", e.getLocalizedMessage());
            e.getStackTrace();
        }
    }

    private void flushAofFile()  {
        try {
            commonFacadeServ.flushAofBackupFileContent();
        } catch (Exception e) {
            log.debug("Exp occurred in flushAofFile {}", e.getLocalizedMessage());
            e.getStackTrace();
        }
    }

    @PostConstruct
    public void init() {
        executorService.scheduleWithFixedDelay(this::cleanUpExpiredValue, initialCleanupExpValTaskDelay,
                cleanupExpValTaskDelay, TimeUnit.SECONDS);
        executorService.scheduleWithFixedDelay(this::flushAofFile, initialFlushValTaskDelay,
                flushValTaskDelay, TimeUnit.SECONDS);
    }
}
