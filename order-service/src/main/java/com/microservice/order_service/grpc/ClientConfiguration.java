package com.microservice.order_service.grpc;

import com.microservice.grpc.InventoryServiceGrpc;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@Slf4j
public class ClientConfiguration
{

	@Autowired
	private EurekaClient discoveryClient;

	@Bean
	public Channel grpcChannel() {
		InstanceInfo instance = discoveryClient.getNextServerFromEureka("inventory-service", false);

		if (instance == null) {
			throw new RuntimeException("No gRPC service instances found");
		}
		log.info(instance.toString());
		Map<String, String> map = instance.getMetadata();
		ManagedChannel channel = ManagedChannelBuilder.forAddress(map.get("grpc-server"),
		                                                          Integer.parseInt(map.get("grpc-port")))
				.usePlaintext() // Use only for development
				.build();
		log.info("Successfully connect gRPC Server");
		return channel;
	}


	@Bean
	public InventoryServiceGrpc.InventoryServiceBlockingStub inventoryServiceBlockingStub(Channel grpcChannel){
		return InventoryServiceGrpc.newBlockingStub(grpcChannel);
	}
}
