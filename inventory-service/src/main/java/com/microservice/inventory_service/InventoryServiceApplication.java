package com.microservice.inventory_service;

import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Lazy;

@SpringBootApplication
@EnableDiscoveryClient
public class InventoryServiceApplication {


	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

}
