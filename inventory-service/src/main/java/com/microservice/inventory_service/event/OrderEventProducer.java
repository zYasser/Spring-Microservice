package com.microservice.inventory_service.event;


import com.microservice.inventory_service.dto.OrderDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class OrderEventProducer
{


	private final KafkaTemplate<String, OrderDto> kafkaTemplate;


	public OrderEventProducer(KafkaTemplate<String, OrderDto> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}


	public void issueRefundEvent(OrderDto order) {
		CompletableFuture<SendResult<String, OrderDto>> event =
				kafkaTemplate.send("order-status", order.getId(), order);
		event.whenComplete((res, err) -> {
			System.out.println("result = " + res);
			if (err != null) {
				System.out.println(" =============================================================== ");
				System.out.println("error = " + err);
			}
			System.out.println("Refund has been sent to message broker.");
		});
	}
}
