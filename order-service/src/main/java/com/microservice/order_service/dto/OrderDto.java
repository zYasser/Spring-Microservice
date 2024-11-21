package com.microservice.order_service.dto;


import lombok.Data;

import java.io.Serializable;

@Data
public class OrderDto implements Serializable
{

    private String id;
    private String productId;
    private int quantity;

    private boolean isRefund = false;
}
