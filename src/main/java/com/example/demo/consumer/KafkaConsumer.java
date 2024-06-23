package com.example.demo.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "alarm", groupId = "demo-group")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
    }
}