package com.fachri.aproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;

@SpringBootApplication(
  exclude = {
    LiquibaseAutoConfiguration.class
  }
)
public class AprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(AprojectApplication.class, args);
	}

}
