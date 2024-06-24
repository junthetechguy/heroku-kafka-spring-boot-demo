package com.example.demo.service;

import com.example.demo.exception.ErrorCode;
import com.example.demo.exception.SnsApplicationException;
import com.example.demo.model.entity.AlarmArgs;
import com.example.demo.model.entity.AlarmEntity;
import com.example.demo.model.entity.AlarmType;
import com.example.demo.model.entity.UserEntity;
import com.example.demo.repository.AlarmEntityRepository;
import com.example.demo.repository.EmitterRepository;
import com.example.demo.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final static Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final static String ALARM_NAME = "alarm";
    private final EmitterRepository emitterRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final UserEntityRepository userEntityRepository;

    public void send(AlarmType type, AlarmArgs arg, Integer receiverUserId) {
        UserEntity user = userEntityRepository.findById(receiverUserId).orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND));
        AlarmEntity alarmEntity = alarmEntityRepository.save(AlarmEntity.of(user, type, arg));


        emitterRepository.get(receiverUserId).ifPresentOrElse(sseEmitter -> {
            try {
                sseEmitter.send(SseEmitter.event().id(alarmEntity.getId().toString()).name(ALARM_NAME).data("new alarm"));
            } catch (IOException e) {
                emitterRepository.delete(receiverUserId);
                throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
            }
        }, () -> log.info("No emitter found."));
    }

    public SseEmitter connectAlarm(Integer userId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, sseEmitter);

        sseEmitter.onCompletion(() -> emitterRepository.delete(userId));
        sseEmitter.onTimeout(() -> emitterRepository.delete(userId));

        try {
            sseEmitter.send(SseEmitter.event()
                    .id("")
                    .name(ALARM_NAME)
                    .data("connect completed"));
        }catch (IOException exception) {
            throw new SnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
        }

        return sseEmitter;
    }
}
