package com.microservice.inventory_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("products")
@ToString

public class Product {
    @Id
    private String id;
    private int quantity;


    private String name;
    private String price;


}
