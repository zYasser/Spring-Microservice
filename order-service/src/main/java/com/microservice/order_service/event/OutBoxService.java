package com.microservice.order_service.event;

import com.microservice.order_service.dto.OrderDto;
import com.microservice.order_service.repository.OrderEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
public class OutBoxService


{
	private final OrderEventRepository orderEventRepository;
	private final KafkaTemplate<String, OrderDto> kafkaTemplate;

	public OutBoxService(OrderEventRepository orderEventRepository, KafkaTemplate<String, OrderDto> kafkaTemplate) {
		this.orderEventRepository = orderEventRepository;
		this.kafkaTemplate = kafkaTemplate;
	}
	@Async
	public void sendEventToKafka(OrderDto orderDto, String key){

		CompletableFuture<SendResult<String, OrderDto>> event = kafkaTemplate.send("orders", orderDto.getId(), orderDto);
		log.info("Sending transaction ID:{} to message broker" , key);

		event.whenComplete((result, err) -> {
			if (err != null) {
				log.error("Failed To Send Order with transaction id:{}  to message broker:{}" , key,err.getMessage());
			}else{

				log.info("Order with transaction id {} has been sent to message broker waiting to get consumed" , key);
				orderEventRepository.removeByKey(key);
			}
		});

	}


}
