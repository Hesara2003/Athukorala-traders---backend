package com.example.hardwaremanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HardwareManagementApplication {
	public static void main(String[] args) {
		SpringApplication.run(HardwareManagementApplication.class, args);
	}

}
