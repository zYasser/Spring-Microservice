package com.microservice.order_service.repository;

import com.microservice.order_service.dto.OrderDto;
import com.microservice.order_service.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class OrderEventRepository
{
	private final RedisTemplate<String, Object> redisTemplate;

	public OrderEventRepository(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public OrderDto getOrderByKey(String key){
		return (OrderDto) redisTemplate.opsForValue().get(key);

	}

	public String save(OrderDto order){
		log.info("Saving Idempotency key");
			try {
				String id = UUID.randomUUID().toString();
				redisTemplate.opsForValue().set(id, order);
				log.info("Event ID: {} has been persisted " , id);

				return id;

			}catch (Exception e){
				e.printStackTrace();
				return null;
			}
	}
	public boolean removeByKey(String key){
		return Boolean.TRUE.equals(redisTemplate.delete(key));
	}
	public Map<String,OrderDto> getAll(){
		Map<String,OrderDto> res= new HashMap<String,OrderDto>();
		log.info("Fetching Write ahead logs from redis");
		Set<String> keys = redisTemplate.keys("*");
		if(keys==null){
			log.info("No keys WAL available");
			return null;
		}
		for(String k : keys){
			OrderDto o = (OrderDto)redisTemplate.opsForValue().get(k);
			if(o!=null)res.put(k,o);

		}
		return res;
	}
}
