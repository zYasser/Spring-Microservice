package com.microservice.order_service.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservice.order_service.dto.OrderDto;
import com.microservice.order_service.entity.Order;
import com.microservice.order_service.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderController
{


	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.ACCEPTED)
	public void placeOrder(@RequestBody OrderDto order) throws JsonProcessingException {
		log.info("received request with id:{} Sending Request to inventory service , with quantity: {}",
		         order.getProductId(), order.getQuantity());
		orderService.saveOrder(order);

	}

	@GetMapping
	public List<Order> getAllOrder() {
		return orderService.getAllOrders();
	}

	@PostMapping("/available")
	public ResponseEntity<Boolean> isAvailable(@RequestBody OrderDto order) {
		log.info(
				"received request to isAvailable Method with id:{} Sending Request to inventory service , with quantity: {}",
				order.getProductId(), order.getQuantity());
		boolean result = orderService.checkQuantity(order);
		return ResponseEntity.status(200).body(result);
	}


}
//	@PostMapping
//	public ResponseEntity<BaseResponse<Order>> placeOrder(@RequestBody OrderDto order) {
//		log.info("received request with id:{} Sending Request to inventory service , with quantity: {}", order.getId(),
//		         order.getQuantity());
//		ResponseEntity<BaseResponse<Boolean>> orderResponseEntity =
//				inventoryClient.reserveProduct(order);
//		log.info(String.valueOf(orderResponseEntity.getBody()));
//
//		if (!orderResponseEntity.getStatusCode()
//				.is2xxSuccessful() || orderResponseEntity.getBody().error != null) {
//			log.error("Fail To Update Stock, {}", orderResponseEntity.getBody().error);
//			return ResponseEntity.status(orderResponseEntity.getStatusCode())
//					.body(BaseResponse.<Order>builder().statusCode(
//									orderResponseEntity.getStatusCode().value()).data(null)
//							      .error(orderResponseEntity.getBody().error).message(null).build());
//		}
//		Order order1 = Order.builder()
//				.price(String.valueOf(Math.random() * 1000)).productId(order.getId()).build();
//
//
//		Order r = orderRepository.save(order1);
//
//
//		log.info("Request finished , {} \n status:{} ", orderResponseEntity, orderResponseEntity.getStatusCode());
//		log.info(String.valueOf(orderResponseEntity.getBody()));
//		return ResponseEntity.status(orderResponseEntity.getStatusCode())
//				.body(BaseResponse.<Order>builder().statusCode(
//								200).data(r)
//						      .error(null).message("Order Placed Successfully").build());
//
//	}

