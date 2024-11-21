package com.microservice.inventory_service.repository;


import com.microservice.inventory_service.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, String>
{
	@Modifying
	@Transactional
	@Query("UPDATE products SET quantity = quantity - :quantity WHERE id = :id AND quantity >= :quantity")
	int decrementProductQuantityIfAvailable(String id, int quantity );

	@Query("UPDATE products SET quantity = quantity + :quantity WHERE id = :id")
	int updateStock(String id, int quantity );


}
