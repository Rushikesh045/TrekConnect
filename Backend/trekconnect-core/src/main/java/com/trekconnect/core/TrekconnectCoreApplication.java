package com.trekconnect.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class TrekconnectCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrekconnectCoreApplication.class, args);
	}

}
