package com.microservice.inventory_service.repository;


import com.microservice.inventory_service.entity.Product;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface ProductRepository extends MongoRepository<Product, String>
{


	@Query("{ '_id' : ObjectId(?0), 'quantity' : { $gte : ?1 } }")
	@Update("{ $inc : { 'quantity' :  ?2 } }")
	int updateStock(String id, int quantity , int decrease);
}
