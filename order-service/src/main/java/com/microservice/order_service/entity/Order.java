package com.microservice.order_service.entity;

import lombok.Builder;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Builder
@Document("orders")
@ToString
public class Order
{
	@Id
	public String id;

	public String price;

	public String productId;


}
