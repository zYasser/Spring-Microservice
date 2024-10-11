package com.microservice.order_service.entity;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;


@Builder
@Document("orders")
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
