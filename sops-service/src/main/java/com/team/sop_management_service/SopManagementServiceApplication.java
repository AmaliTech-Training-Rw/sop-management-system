package com.team.sop_management_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SopManagementServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(SopManagementServiceApplication.class, args);
	}

}