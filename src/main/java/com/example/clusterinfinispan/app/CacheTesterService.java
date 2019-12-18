package com.example.clusterinfinispan.app;

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service
public class CacheTesterService {

    private Logger log = LoggerFactory.getLogger(CacheTesterService.class);

    private final EmbeddedCacheManager embeddedCacheManager;

    private int getCounter = 0;
    private int putCounter = 0;

    public static final String KEY = "CACHE_KEY";

    private String nodeName;
    public CacheTesterService(EmbeddedCacheManager embeddedCacheManager, @Value("${node.name}") String nodeName) {
        this.embeddedCacheManager = embeddedCacheManager;
        this.nodeName = nodeName;
    }

    public void getOrPut() {
        Cache<Object, Object> cache = embeddedCacheManager.getCache("spring-cache");
        log.info("GET #{}", ++getCounter);
        Object message = cache.get(KEY);

        if(message != null) {
            log.info("HIT '{}'", message);
            return;
        }
        log.warn("MISS");
        String value = "value-" + nodeName + "-" + ++putCounter;
        log.warn("PUT " + value);
        cache.put(KEY, value);

    }

    public void clear() {
        Cache<Object, Object> cache = embeddedCacheManager.getCache("spring-cache");
        log.warn("REMOVE");
        Object removedValue = cache.remove(KEY);
        log.warn("REMOVE '{}'", removedValue);
    }

    @Cacheable(cacheNames = "spring-cache", key = "'greeting'")
    public String hello() {
        return "hello!";
    }

}
