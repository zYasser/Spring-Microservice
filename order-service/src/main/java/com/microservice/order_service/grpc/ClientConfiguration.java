package com.microservice.order_service.grpc;

import com.microservice.grpc.InventoryServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration
{

	@Autowired
	private LoadBalancerClient loadBalancerClient;

	@Bean
	public InventoryServiceGrpc.InventoryServiceBlockingStub inventoryServiceBlockingStub() {
		ServiceInstance serviceInstance = loadBalancerClient
				.choose("inventory-service");

		if (serviceInstance == null) {
			throw new RuntimeException("No gRPC service instances found");
		}

		ManagedChannel channel = ManagedChannelBuilder
				.forAddress(serviceInstance.getHost(), serviceInstance.getPort())
				.usePlaintext() // Use only for development
				.build();

		return InventoryServiceGrpc.newBlockingStub(channel);
	}
}
