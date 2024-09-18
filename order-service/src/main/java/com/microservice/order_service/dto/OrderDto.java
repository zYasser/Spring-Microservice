package com.microservice.order_service.dto;


import lombok.Data;

@Data
public class OrderDto


{
	private String id;
	private int quantity;
	private boolean isRefund=false;
}
