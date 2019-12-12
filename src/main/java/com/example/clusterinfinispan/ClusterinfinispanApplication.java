package com.example.clusterinfinispan;

import com.example.clusterinfinispan.app.CacheTesterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class ClusterinfinispanApplication implements CommandLineRunner {

	private Logger log = LoggerFactory.getLogger(ClusterinfinispanApplication.class);

	final CacheTesterService cacheTesterService;

	private int ok = 0;
	private int ko = 0;

	public ClusterinfinispanApplication(CacheTesterService cacheTesterService) {
		this.cacheTesterService = cacheTesterService;
	}

	public static void main(String[] args) {
		SpringApplication.run(ClusterinfinispanApplication.class, args);
	}

	@Override
	public void run(String... args) throws InterruptedException {
		log.info("Application now running... press CTRL+C to exit");
		Thread.currentThread().join();
	}

	@Scheduled(fixedDelay = 5000)
	public void clearCache() {
		try {
			cacheTesterService.clear();
			ok++;
		} catch (Exception e) {
			log.error(e.getMessage());
			ko++;
//			System.exit(1);
		}

		log.info("{} success, {} failed", ok, ko);

	}

	@Scheduled(fixedDelay = 50)
	void getFromCache() {
		try {
			cacheTesterService.getOrPut();
			ok++;
		} catch (Exception e) {
			log.error(e.getMessage());
			ko++;
//			System.exit(1);
		}
		log.info("{} success, {} failed", ok, ko);
	}
}
