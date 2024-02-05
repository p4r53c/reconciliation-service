package com.msr.rnip.reconciliation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan("*.properties")
@EnableScheduling
public class ReconciliationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReconciliationServiceApplication.class, args);
		System.out.println("Reconciliation Service Initialized!");

	}

}
