package com.microservice.api_gateway.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {


    @GetMapping("/inventory")
    public ResponseEntity<String> fallbackInventoryService() {
        return new ResponseEntity<>("The inventory service is not available now, please try again later", HttpStatus.SERVICE_UNAVAILABLE);
    }

}
