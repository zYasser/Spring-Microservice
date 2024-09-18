package com.microservice.order_service.feignClient;


import com.microservice.order_service.config.InventoryFallbackFactory;
import com.microservice.order_service.dto.BaseResponse;
import com.microservice.order_service.dto.OrderDto;
import com.microservice.order_service.dto.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "INVENTORY-SERVICE", fallbackFactory = InventoryFallbackFactory.class)
public interface InventoryClient
{


	@GetMapping("/api/products/{id}")
	ResponseEntity<Product> getStock(@PathVariable("id") String id);


	@PatchMapping(value = "/api/products/stock", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<BaseResponse<Boolean>> reserveProduct(OrderDto order);

}
