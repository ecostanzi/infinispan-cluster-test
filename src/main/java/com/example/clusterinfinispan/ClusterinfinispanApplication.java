package com.example.clusterinfinispan;

import com.example.clusterinfinispan.app.CacheTesterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class ClusterinfinispanApplication implements CommandLineRunner {

	private Logger log = LoggerFactory.getLogger(ClusterinfinispanApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ClusterinfinispanApplication.class, args);
	}

	final CacheTesterService cacheTesterService;

	private int ok = 0;
	private int ko = 0;

	private List<String> cacheOps;

	public ClusterinfinispanApplication(CacheTesterService cacheTesterService, @Value("${node.cacheOps}") List<String> cacheOps) {
		this.cacheTesterService = cacheTesterService;
		this.cacheOps = cacheOps;
	}

	@Override
	public void run(String... args) throws InterruptedException {
		log.info("Application now running... press CTRL+C to exit");
		cacheTesterService.hello();
		Thread.currentThread().join();
	}

	@Scheduled(fixedDelay = 5000)
	public void clearCache() {
		if(cacheOps.contains("delete")) {
			try {
				cacheTesterService.clear();
				ok++;
			} catch (Exception e) {
				log.error(e.getMessage());
				ko++;
			}

			log.info("{} success, {} failed", ok, ko);
		}
	}

	@Scheduled(fixedDelay = 50)
	void getFromCache() {
		if(cacheOps.contains("write")) {
			try {
				cacheTesterService.getOrPut();
				ok++;
			} catch (Exception e) {
				log.error(e.getMessage());
				ko++;
			}
			log.info("{} success, {} failed", ok, ko);
		}
	}
}
