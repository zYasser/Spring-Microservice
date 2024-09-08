package com.microservice.order_service.repository;


import com.microservice.order_service.entity.Order;
import org.springframework.data.mongodb.core.annotation.Collation;
import org.springframework.data.mongodb.repository.MongoRepository;

@Collation
public interface OrderRepository extends MongoRepository<Order, String> {
}
