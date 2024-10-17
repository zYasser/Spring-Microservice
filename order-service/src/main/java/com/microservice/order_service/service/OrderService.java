package com.microservice.order_service.service;

import com.microservice.order_service.dto.OrderDto;
import com.microservice.order_service.entity.Order;
import com.microservice.order_service.entity.OrderStatus;
import com.microservice.order_service.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderDto> kafkaTemplate;

    public OrderService(OrderRepository orderRepository, KafkaTemplate<String, OrderDto> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }


    public void saveOrder(OrderDto request) {
        Order order1 = Order.builder().price((float) (Math.random() * 10000)).productId(request.getProductId()).status(OrderStatus.PENDING).build();
        orderRepository.save(order1);
        System.out.println("order1 = " + order1);
        request.setId(order1.getId());
        CompletableFuture<SendResult<String, OrderDto>> event = kafkaTemplate.send("orders", request.getId(), request);
        log.info("Order ");
        event.whenComplete((result, err) -> {
            System.out.println("result = " + result);
            if (err != null) {
                System.out.println(" =============================================================== ");
                System.out.println("error = " + err);
            }
            System.out.println("Order has been sent to message broker waiting to get consumed");
        });



    }

    public void updateOrder(Order order) {
        Optional<Order> result = orderRepository.findById(order.getId());
        if (result.isEmpty()) {
            log.error("Order With id {} doesn't exist", order.getId());
            return;
        }
        order.setPrice(result.get().getPrice());
        orderRepository.save(order);
    }
}
