package com.jeffrey.processimageservice.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


@Component
@Slf4j
public class FlushCache {
    private final TreeMap<Long, File> cacheTaskMap;

    @Autowired
    public FlushCache(TreeMap<Long, File> cacheTaskMap) {
        this.cacheTaskMap = cacheTaskMap;
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void flushCache(){
        log.debug("开始检查过期缓存");
        long l = System.currentTimeMillis();
        for (Map.Entry<Long, File> item : cacheTaskMap.entrySet()) {
            Long key = item.getKey();
            File value = item.getValue();
            if (l >= key) {
                if (value.delete()) {
                    cacheTaskMap.remove(key);
                    log.info("移除过期缓存数据：{}", item.getValue().getName());
                }
            }
        }
        log.debug("检查结束");
    }
}
