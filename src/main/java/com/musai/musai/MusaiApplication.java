package com.musai.musai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MusaiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusaiApplication.class, args);
	}

}