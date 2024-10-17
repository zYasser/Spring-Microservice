    package com.microservice.order_service.event;

    import com.microservice.order_service.dto.OrderDto;
    import com.microservice.order_service.entity.Order;
    import com.microservice.order_service.entity.OrderStatus;
    import com.microservice.order_service.service.OrderService;
    import org.springframework.kafka.annotation.KafkaListener;
    import org.springframework.messaging.handler.annotation.Payload;

    public class OrderEventHandler {

        private final OrderService orderService;

        public OrderEventHandler(OrderService orderService) {
            this.orderService = orderService;
        }

        @KafkaListener(groupId = "group-1", topics = "order-status", concurrency = "1")
        public void refundEventHandler(@Payload OrderDto orderDto) {
            if (orderDto.isRefund()) {
                Order order = Order.builder()
                        .productId(orderDto.getProductId())
                        .status(OrderStatus.REFUNDED)
                        .id(orderDto.getProductId())
                        .build();
                orderService.updateOrder(order);
            }

        }
    }
