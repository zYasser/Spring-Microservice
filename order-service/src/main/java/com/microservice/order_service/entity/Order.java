package com.microservice.order_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;


@Builder
@Entity(name = "orders")
@ToString
@Data
public class Order
{
	@Id
	private String id;

	private float price;

	private String productId;

	private OrderStatus status;

}
