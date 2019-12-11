package com.example.clusterinfinispan.app;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.eviction.EvictionType;
import org.infinispan.spring.starter.embedded.InfinispanCacheConfigurer;
import org.infinispan.spring.starter.embedded.InfinispanGlobalConfigurer;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.util.concurrent.IsolationLevel;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    public InfinispanGlobalConfigurer globalConfiguration() {
        return () -> GlobalConfigurationBuilder
                .defaultClusteredBuilder()
                .transport().defaultTransport()
                .addProperty("configurationFile", "jgroups-config.xml")
                .clusterName("infinispan-demo-cluster").globalJmxStatistics()
                .enabled(true)
                .allowDuplicateDomains(true)
            .build();
    }

    @Bean
    public InfinispanCacheConfigurer cacheConfigurer() {

        return manager -> {

            org.infinispan.configuration.cache.Configuration invalidationCacheConfiguration = new ConfigurationBuilder()
                .transaction()
                    .lockingMode(LockingMode.OPTIMISTIC)
                    .transactionMode(TransactionMode.NON_TRANSACTIONAL)
                .persistence()
                    .addClusterLoader()
                    .remoteCallTimeout(800)
                .clustering()
                    .cacheMode(CacheMode.INVALIDATION_SYNC)
                .jmxStatistics().enabled(false)
                .locking()
                    .concurrencyLevel(1000)
                    .lockAcquisitionTimeout(15, TimeUnit.SECONDS)
                    .isolationLevel(IsolationLevel.REPEATABLE_READ)
                .eviction()
                    .type(EvictionType.COUNT).size(1000)
                .expiration()
                    .lifespan(10, TimeUnit.SECONDS)
                .build();

            manager.defineConfiguration("spring-cache", invalidationCacheConfiguration);
        };
    }
}
