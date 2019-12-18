package com.example.clusterinfinispan;

import com.example.clusterinfinispan.app.CacheTesterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;

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

	private String nodeType;

	public ClusterinfinispanApplication(CacheTesterService cacheTesterService, Environment environment) {
		this.cacheTesterService = cacheTesterService;
		boolean master = Arrays.asList(environment.getActiveProfiles()).contains("master");
		nodeType = master ? "master" : "slave";
	}

	@Override
	public void run(String... args) throws InterruptedException {
		log.info("Application now running... press CTRL+C to exit");
		cacheTesterService.hello();
		Thread.currentThread().join();
	}

	@Scheduled(fixedDelay = 5000)
	public void clearCache() {
		if(nodeType.equals("master")) {
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
		if(nodeType.equals("slave")) {
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
