package com.example.clusterinfinispan.app;

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service
public class CacheTesterService {

    private Logger log = LoggerFactory.getLogger(CacheTesterService.class);

    private final Environment environment;
    private final EmbeddedCacheManager embeddedCacheManager;

    private String nodeType;
    private int counter = 0;

    public static final String KEY = "CACHE_KEY";

    public CacheTesterService(Environment environment, EmbeddedCacheManager embeddedCacheManager) {
        this.environment = environment;
        this.embeddedCacheManager = embeddedCacheManager;
    }

    public void getOrPut() {
        Cache<Object, Object> cache = embeddedCacheManager.getCache("spring-cache");
        log.info("GET");
        Object message = cache.get(KEY);

        if(message != null) {
            log.info("HIT '{}'", message);
            return;
        }

        String value = "value-" + nodeType + "-" + ++counter;
        log.warn("PUT " + value);
        cache.put(KEY, value);

    }

    public void clear() {
        if(nodeType.equals("master")){
            Cache<Object, Object> cache = embeddedCacheManager.getCache("spring-cache");
            log.warn("REMOVE");
            Object removedValue = cache.remove(KEY);
            log.warn("REMOVE '{}'", removedValue);
        }
    }

    @PostConstruct
    public void postConstruct(){
        boolean master = Arrays.asList(environment.getActiveProfiles()).contains("master");
        nodeType = master ? "master" : "slave";
    }
}
