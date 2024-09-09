package com.microservice.inventory_service.repository;


import com.microservice.inventory_service.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository  extends MongoRepository<Product, String> {
}
