package com.microservice.order_service.service;

import com.microservice.order_service.dto.OrderDto;
import com.microservice.order_service.entity.Order;
import com.microservice.order_service.entity.OrderStatus;
import com.microservice.order_service.event.OutBoxService;
import com.microservice.order_service.repository.OrderEventRepository;
import com.microservice.order_service.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderService
{

	private final OrderRepository orderRepository;
	private final OrderEventRepository orderEventRepository;
	private final OutBoxService outBoxService;
	public OrderService(OrderRepository orderRepository,
	                    OrderEventRepository orderEventRepository, OutBoxService outBoxService) {
		this.orderRepository = orderRepository;
		this.orderEventRepository = orderEventRepository;
		this.outBoxService = outBoxService;
	}


	public void saveOrder(OrderDto request) {
		Order order1 = Order.builder().price((float) (Math.random() * 10000)).productId(request.getProductId())
				.status(OrderStatus.PENDING).build();
		orderRepository.save(order1);
		System.out.println("order1 = " + order1);

		request.setId(order1.getId());
		String idempotency = orderEventRepository.save(request);
		outBoxService.sendEventToKafka(request, idempotency);


	}

	public List<Order> getAllOrders(){
		return orderRepository.findAll();
	}

	public void updateOrder(Order order) {
		Optional<Order> result = orderRepository.findById(order.getId());
		if (result.isEmpty()) {
			log.error("Order With id {} doesn't exist", order.getId());
			return;
		}
		orderRepository.save(order);
		log.info("Order has been updated");
	}
}
