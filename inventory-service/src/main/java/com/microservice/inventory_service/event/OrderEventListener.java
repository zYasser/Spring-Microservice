package com.microservice.inventory_service.event;

import com.microservice.inventory_service.service.ProductService;
import com.microservice.inventory_service.dto.OrderDto;
import com.microservice.inventory_service.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderEventListener
{

	private final ProductService productService;

    public OrderEventListener(ProductService productService) {
        this.productService = productService;
    }

    @KafkaListener(groupId = "group-1", topics = "orders" , concurrency = "1")
	public void consume(@Payload OrderDto order) throws ResourceNotFoundException {
		log.info("We received the Order : {}", order);
		productService.orderEvent(order);

	}


}
