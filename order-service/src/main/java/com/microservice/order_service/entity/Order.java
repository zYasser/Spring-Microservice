package com.microservice.order_service.entity;

import org.springframework.data.annotation.Id;

import java.util.List;

public class Order {
    @Id
    public String id;

    public String price;

    public List<String> productId;



}
