package com.microservice.order_service.service;

import com.microservice.grpc.CheckQuantityRequest;
import com.microservice.grpc.CheckQuantityResponse;
import com.microservice.grpc.InventoryServiceGrpc;
import com.microservice.order_service.dto.OrderDto;
import com.microservice.order_service.entity.Order;
import com.microservice.order_service.entity.OrderStatus;
import com.microservice.order_service.event.OutBoxService;
import com.microservice.order_service.repository.OrderEventRepository;
import com.microservice.order_service.repository.OrderRepository;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderService
{


	private final InventoryServiceGrpc.InventoryServiceBlockingStub inventoryServiceBlockingStub;


	private final OrderRepository orderRepository;
	private final OrderEventRepository orderEventRepository;
	private final OutBoxService outBoxService;

	public OrderService(InventoryServiceGrpc.InventoryServiceBlockingStub inventoryServiceBlockingStub,
	                    OrderRepository orderRepository, OrderEventRepository orderEventRepository,
	                    OutBoxService outBoxService) {
		this.inventoryServiceBlockingStub = inventoryServiceBlockingStub;
		this.orderRepository = orderRepository;
		this.orderEventRepository = orderEventRepository;
		this.outBoxService = outBoxService;
	}


	public void saveOrder(OrderDto request) {
		Order order1 = Order.builder().price((float) (Math.random() * 10000)).productId(request.getProductId()).status(
				OrderStatus.PENDING).build();
		orderRepository.save(order1);
		System.out.println("order1 = " + order1);

		request.setId(order1.getId());
		String idempotency = orderEventRepository.save(request);
		outBoxService.sendEventToKafka(request, idempotency);


	}

	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	public boolean checkQuantity(OrderDto request) {
		try {
			CheckQuantityResponse result = inventoryServiceBlockingStub.checkQuantity(
					CheckQuantityRequest.newBuilder().setQuantity(request.getQuantity())
							.setProductId(request.getProductId()).build());
		} catch (StatusRuntimeException e) {
			System.out.println("e.getMessage() = " + e.getMessage() + "Status Error " + e.getStatus());
		}


		return true;
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
