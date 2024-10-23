package com.microservice.inventory_service.service;

import com.microservice.inventory_service.dto.BaseResponse;
import com.microservice.inventory_service.dto.OrderDto;
import com.microservice.inventory_service.entity.Product;
import com.microservice.inventory_service.event.OrderEventProducer;
import com.microservice.inventory_service.exceptions.ResourceNotFoundException;
import com.microservice.inventory_service.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ProductService {


    private final ProductRepository productRepository;



    private final OrderEventProducer orderEventProducer;

    public ProductService(ProductRepository productRepository, OrderEventProducer orderEventProducer) {
        this.productRepository = productRepository;
        this.orderEventProducer = orderEventProducer;
    }

    public BaseResponse<Boolean> updateStock(OrderDto order) throws ResourceNotFoundException {
        Product product = productRepository.findById(order.getProductId()).orElseThrow(() -> {
            log.error("Product doesn't exist");
            return new ResourceNotFoundException("Product doesn't exist");
        });
        if (product.getQuantity() == 0) return new BaseResponse<Boolean>(null, null, "Item is out of stock", 200);
        if (product.getQuantity() < order.getQuantity()) {
            String message = String.format(
                    "Insufficient stock available. Only %d items are in stock, but you want to purchase %d.",
                    product.getQuantity(), order.getQuantity());
            return new BaseResponse<Boolean>(null, null, message, 200);
        }
        int updatedRows = productRepository.updateStock(order.getProductId(), order.getQuantity(), order.getQuantity() * -1);
        log.info("Product has been reserved, stock updated. Rows affected: {}", updatedRows);
        if (updatedRows == 0) return new BaseResponse<Boolean>(null, null, "Item is out of stock", 200);

        return new BaseResponse<Boolean>(true, "Stock updated successfully", null, 200);
    }


    public void refund(OrderDto order) {
        Optional<Product> result = productRepository.findById(order.getProductId());
        if (result.isEmpty()) {
            log.info("There's no product with id {}", order.getProductId());
            return;
        }
        Product product = result.get();
        int updatedRows = productRepository.updateStock(order.getProductId(), order.getQuantity(), order.getQuantity());
        log.info("Product quantity has been updated, stock updated. Rows affected: {}", updatedRows);
        if (updatedRows == 0) {
            log.info("Something went wrong , Failed To update stock product: {} doesn't exist", order.getProductId());
        }
        log.info("Product:{} has been refunded successfully ", order.getProductId());


    }

    public void orderEvent(OrderDto order) throws ResourceNotFoundException {
        Optional<Product> result = productRepository.findById(order.getProductId());
        if (result.isEmpty()) {
            log.info("There's no product with id {}", order.getProductId());
            return;
        }
        Product product = result.get();

        if (product.getQuantity() == 0 || product.getQuantity() < order.getQuantity()) {
            String message = String.format(
                    "Insufficient stock available. Only %d items are in stock, but you want to purchase %d.",
                    product.getQuantity(), order.getQuantity());

            log.error("Item {} out of stock, Issuing refund on order with id: {}", order.getProductId(), order.getId());

            order.setRefund(true);
            orderEventProducer.issueRefundEvent(order);

            return;
        }
        int updatedRows = productRepository.updateStock(order.getProductId(), order.getQuantity(), order.getQuantity() * -1);
        log.info("Product has been reserved, stock updated. Rows affected: {}", updatedRows);
        if (updatedRows == 0) {
            log.info("Something went wrong , Failed To update stock product: {} doesn't exist", order.getProductId());
        }
        log.info("Product:{} has been reserved successfully ", order.getProductId());

    }


}