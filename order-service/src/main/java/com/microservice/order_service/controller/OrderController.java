package com.microservice.order_service.controller;


import com.microservice.order_service.dto.BaseResponse;
import com.microservice.order_service.dto.OrderDto;
import com.microservice.order_service.feignClient.InventoryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderController
{


	private final InventoryClient inventoryClient;

	public OrderController(InventoryClient inventoryClient) {
		this.inventoryClient = inventoryClient;
	}

	@PostMapping
	public BaseResponse<Boolean> placeOrder(@RequestBody OrderDto order) {
		log.info("received request with id:{} Sending Request to inventory service , with quantity: {}", order.getId(),
		         order.getQuantity());
		ResponseEntity<BaseResponse<Boolean>> orderResponseEntity =
				inventoryClient.reserveProduct(order);

		log.info(String.valueOf(orderResponseEntity.getBody()));

		if(!orderResponseEntity.getStatusCode().is2xxSuccessful() || orderResponseEntity.getBody().data==null ){
				//

		}
		log.info("Request finished , {} \n status:{} ", orderResponseEntity, orderResponseEntity.getStatusCode());
		log.info(String.valueOf(orderResponseEntity.getBody()));
		return orderResponseEntity.getBody();
	}


}
