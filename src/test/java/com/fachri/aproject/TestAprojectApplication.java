package com.fachri.aproject;

import org.springframework.boot.SpringApplication;

public class TestAprojectApplication {

	public static void main(String[] args) {
		SpringApplication.from(AprojectApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
