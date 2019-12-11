package com.example.clusterinfinispan;

import com.example.clusterinfinispan.app.CacheTesterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StopWatch;

import java.util.stream.IntStream;

@SpringBootApplication
@EnableScheduling
public class ClusterinfinispanApplication implements CommandLineRunner {

	private Logger log = LoggerFactory.getLogger(ClusterinfinispanApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ClusterinfinispanApplication.class, args);
	}

	@Autowired
	CacheTesterService cacheTesterService;

	@Override
	public void run(String... args) {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		IntStream.range(0, Integer.MAX_VALUE).forEach(r-> {
			sleep(100);
			cacheTesterService.getOrPut();
		});
	}

	@Scheduled(fixedDelay = 5000)
	public void clearCache() {
		cacheTesterService.clear();

	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
