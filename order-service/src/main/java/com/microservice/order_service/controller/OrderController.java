package com.microservice.order_service.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.order_service.dto.BaseResponse;
import com.microservice.order_service.dto.OrderDto;
import com.microservice.order_service.entity.Order;
import com.microservice.order_service.feignClient.InventoryClient;
import com.microservice.order_service.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderController {


    private final KafkaTemplate<String, OrderDto> kafkaTemplate;

    private final OrderRepository orderRepository;

    private final InventoryClient inventoryClient;

    public OrderController(KafkaTemplate kafkaTemplate, OrderRepository orderRepository, InventoryClient inventoryClient) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
    }

    @PostMapping
    public void placeOrder(@RequestBody OrderDto order) throws JsonProcessingException {
        log.info("received request with id:{} Sending Request to inventory service , with quantity: {}", order.getProductId(), order.getQuantity());
//		ResponseEntity<BaseResponse<Boolean>> orderResponseEntity =
//				inventoryClient.reserveProduct(order);
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
//		ObjectMapper objectMapper = new ObjectMapper();
//		String jsonValue = objectMapper.writeValueAsString(order);

        Order order1 = Order.builder().price((float) (Math.random() * 10000)).productId(order.getProductId()).build();
        orderRepository.save(order1);
        order.setId(order1.getId());
        CompletableFuture<SendResult<String, OrderDto>> event = kafkaTemplate.send("orders", order.getId(), order);
        event.whenComplete((result, err) -> {
            System.out.println("result = " + result);
            if (err != null) {
                System.out.println(" =============================================================== ");
                System.out.println("error = " + err);
            }
            System.out.println("Order has been sent to message broker waiting to get consumed");
        });


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

