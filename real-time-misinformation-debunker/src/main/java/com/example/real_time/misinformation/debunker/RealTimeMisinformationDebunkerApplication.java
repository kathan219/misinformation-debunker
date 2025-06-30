package com.example.real_time.misinformation.debunker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties

public class RealTimeMisinformationDebunkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealTimeMisinformationDebunkerApplication.class, args);
	}

}
