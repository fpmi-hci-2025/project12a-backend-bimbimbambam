package com.example.techstore.order.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    private static final String TOPIC = "order-placed";

    public void sendOrderPlacedEvent(OrderPlacedEvent event) {
        log.info("Sending OrderPlacedEvent for order id: {}", event.getOrderId());
        kafkaTemplate.send(TOPIC, event);
    }
}