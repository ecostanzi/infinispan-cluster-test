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
public class DummyService {

    private Logger log = LoggerFactory.getLogger(DummyService.class);

    private final Environment environment;
    private final EmbeddedCacheManager embeddedCacheManager;

    public static final String KEY = "CACHE_KEY";

    public DummyService(Environment environment, EmbeddedCacheManager embeddedCacheManager) {
        this.environment = environment;
        this.embeddedCacheManager = embeddedCacheManager;
    }

    public void sayHi() {
        Cache<Object, Object> cache = embeddedCacheManager.getCache("spring-cache");
        Object message = cache.get(KEY);

        if(message != null) {
            log.info("HIT '{}'", message);
            return;
        }

        String value = "cache-" + name + "-" + System.currentTimeMillis();
        log.warn("PUT " + value);
        cache.put(KEY, value);

    }

    public void clearCache() {
        if(name.equals("master")){
            Cache<Object, Object> cache = embeddedCacheManager.getCache("spring-cache");
            Object removedValue = cache.remove(KEY);
            log.warn("REMOVE '{}'", removedValue);
        }
    }

    private String name;

    @PostConstruct
    public void postConstruct(){
        boolean master = Arrays.asList(environment.getActiveProfiles()).contains("master");
        name = master ? "master" : "slave";
    }
}
