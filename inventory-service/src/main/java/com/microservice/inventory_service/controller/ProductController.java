package com.microservice.inventory_service.controller;


import com.microservice.inventory_service.service.ProductService;
import com.microservice.inventory_service.dto.BaseResponse;
import com.microservice.inventory_service.dto.OrderDto;
import com.microservice.inventory_service.entity.Product;
import com.microservice.inventory_service.exceptions.ResourceNotFoundException;
import com.microservice.inventory_service.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private ProductService productService;

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
		log.info("Received a request to update stock for product: {}", orderDTO.getId());
		BaseResponse<Boolean> response = productService.updateStock(orderDTO);
		return ResponseEntity.status(response.getStatusCode()).body(response);

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
