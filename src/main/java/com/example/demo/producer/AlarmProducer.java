package com.example.demo.producer;

import com.example.demo.model.event.AlarmEvent;
import com.example.demo.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlarmProducer {
    private final AlarmService alarmService;

    @Async
    public void send(AlarmEvent event) {
        alarmService.send(event.getAlarmType(), event.getArgs(), event.getReceiveUserId());
    }
}
