package com.microservice.inventory_service.Service;

import com.microservice.inventory_service.dto.BaseResponse;
import com.microservice.inventory_service.dto.OrderDto;
import com.microservice.inventory_service.entity.Product;
import com.microservice.inventory_service.exceptions.ResourceNotFoundException;
import com.microservice.inventory_service.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductService
{

	private final ProductRepository productRepository;

	@Autowired
	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public BaseResponse<Boolean> updateStock(OrderDto order) throws ResourceNotFoundException {
		Product product = productRepository.findById(order.getId()).orElseThrow(() -> {
			log.error("Product doesn't exist");
			return new ResourceNotFoundException("Product doesn't exist");
		});
		if (product.getQuantity() == 0) return new BaseResponse<Boolean>(null, null, "Item is out of stock", 200);
		if (product.getQuantity() < order.getQuantity()) {
			String message = String.format(
					"Insufficient stock available. Only %d items are in stock, but you want to purchase %d.",
					product.getQuantity(), order.getQuantity());
			return new BaseResponse<Boolean>(null, null, message, 200);
		}
		int updatedRows = productRepository.updateStock(order.getId(), order.getQuantity(), order.getQuantity() * -1);
		log.info("Product has been reserved, stock updated. Rows affected: {}", updatedRows);
		if (updatedRows == 0) return new BaseResponse<Boolean>(null, null, "Item is out of stock", 200);

		return new BaseResponse<Boolean>(true, "Stock updated successfully", null, 200);
	}


}