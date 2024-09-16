package com.microservice.inventory_service.exceptions;


import org.springframework.http.HttpStatus;

public record ErrorMessage(HttpStatus status,
                            String message
) {
    public ErrorMessage(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
