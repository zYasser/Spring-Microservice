package com.microservice.inventory_service.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrderDto



{
    private String id;
    private int quantity;

    private boolean refund;


    @JsonCreator
    public OrderDto(
            @JsonProperty("id") String id,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("refund") boolean refund) {
        this.id = id;
        this.quantity = quantity;
        this.refund = refund;
    }
}
