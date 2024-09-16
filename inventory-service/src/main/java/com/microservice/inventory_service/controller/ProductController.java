package com.microservice.inventory_service.controller;


import com.microservice.inventory_service.dto.BaseResponse;
import com.microservice.inventory_service.dto.OrderDto;
import com.microservice.inventory_service.entity.Product;
import com.microservice.inventory_service.exceptions.ResourceNotFoundException;
import com.microservice.inventory_service.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@Slf4j
public class ProductController
{

	int attempted = 0;


	@Autowired
	private ProductRepository productRepository;


	// Create
	@PostMapping
	public ResponseEntity<Product> createProduct(@RequestBody Product product) {
		Product savedProduct = productRepository.save(product);
		return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
	}

	// Read (all)
	@GetMapping
	public ResponseEntity<List<Product>> getAllProducts() throws InterruptedException {
		log.info("Received A Request Attempted:{}", ++attempted);
		List<Product> products = productRepository.findAll();
		log.info("Request is finished");
		return new ResponseEntity<>(products, HttpStatus.OK);
	}


	@PatchMapping("/stock")
	public ResponseEntity<BaseResponse<Boolean>> updateStock(@RequestBody OrderDto orderDTO) throws
			ResourceNotFoundException {

		log.info("Received A Request, updating a stock,Product:{} ",orderDTO.toString() );
		try {
			Product p = productRepository.findById(orderDTO.getId())
					.orElseThrow(() -> {
						log.error("Product Doesn't exist");
						return new ResourceNotFoundException("Product doesn't exist");
					});
			log.info("Found The Product");
			if (p.getQuantity() == 0) {
				log.error("Stock is 0");
				return ResponseEntity.ok(new BaseResponse<Boolean>(null, "Item is out of stock", 200));
			} else if (p.getQuantity() < orderDTO.getQuantity()) {
				log.error(String.format(
						"Insufficient stock available. Only %d items are in stock, but you want to purchase %d.",
						p.getQuantity(), orderDTO.getQuantity()));
				String message = String.format(
						"Insufficient stock available. Only %d items are in stock, but you want to purchase %d.",
						p.getQuantity(), orderDTO.getQuantity());
				return ResponseEntity.ok(new BaseResponse<Boolean>(null, message, 200));
			}

			int rows = productRepository.updateStock(orderDTO.getId(), orderDTO.isRefund() ? 0 : orderDTO.getQuantity(),
			                                         orderDTO.getQuantity() * -1);
			log.info("Product has been Reserve, stock got updated, Rows affected :{}", rows);
			if (rows == 0) {
				return ResponseEntity.ok(new BaseResponse<Boolean>(null, "Item is out of stock", 200));
			}

			return ResponseEntity.ok(new BaseResponse<Boolean>(true, "Stock updated successfully", 200));
		} catch (Exception e) {
			log.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new BaseResponse<Boolean>(null, "An error occurred while updating the stock", 500));
		}
	}

	// Read (by id)
	@GetMapping("/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable String id) {
		Optional<Product> product = productRepository.findById(id);
		return product.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	// Update
	@PutMapping("/{id}")
	public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
		Optional<Product> productData = productRepository.findById(id);
		if (productData.isPresent()) {

			Product updatedProduct = productData.get();
			updatedProduct.setName(product.getName());
			updatedProduct.setQuantity(product.getQuantity());
			updatedProduct.setPrice(product.getPrice());
			return new ResponseEntity<>(productRepository.save(updatedProduct), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// Delete
	@DeleteMapping("/{id}")
	public ResponseEntity<HttpStatus> deleteProduct(@PathVariable String id) {
		try {
			productRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
