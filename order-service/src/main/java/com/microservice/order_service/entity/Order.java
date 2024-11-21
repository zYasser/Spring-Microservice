package com.microservice.order_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;


@Builder
@Entity(name = "orders")
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order
{
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	private float price;

	private String productId;


	private OrderStatus status;

}
