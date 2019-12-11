package com.example.clusterinfinispan;

import com.example.clusterinfinispan.app.DummyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.stream.IntStream;
import java.util.stream.Stream;

@SpringBootApplication
@EnableScheduling
public class ClusterinfinispanApplication implements CommandLineRunner {

	private Logger log = LoggerFactory.getLogger(ClusterinfinispanApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ClusterinfinispanApplication.class, args);
	}

	@Autowired
	DummyService dummyService;

	@Override
	public void run(String... args) throws Exception {
		IntStream.range(0, 100000).forEach(r-> {
			sleep(100);

			try {
				dummyService.sayHi();
			} catch (Exception e) {
				log.error(e.getMessage());

			}
		});
	}

	@Scheduled(fixedDelay = 5000)
	public void clearCache() {
		dummyService.clearCache();

	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
