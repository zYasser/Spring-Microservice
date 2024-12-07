package com.microservice.order_service.scheduling;

import com.microservice.order_service.dto.OrderDto;
import com.microservice.order_service.event.OutBoxService;
import com.microservice.order_service.repository.OrderEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class OutBoxTransactionJob
{


//	private final OrderEventRepository orderEventRepository;
//	private final OutBoxService outBoxService;
//	public OutBoxTransactionJob(OrderEventRepository orderEventRepository
//	                            , OutBoxService outBoxService) {
//		this.orderEventRepository = orderEventRepository;
//		this.outBoxService = outBoxService;
//	}
//
//
//	@Scheduled(fixedRate = 10000)
//	public void OrderOutBox(){
//		Map<String,OrderDto> orders=orderEventRepository.getAll();
//		log.info("Retrying delivery of previously failed messages to the destination.");
//		if(orders.isEmpty()){
//			log.info("There are no failed messages");
//		}
//		orders.forEach((key,value)->{
//
//			outBoxService.sendEventToKafka(value,key);
//		});
//
//	}
}
