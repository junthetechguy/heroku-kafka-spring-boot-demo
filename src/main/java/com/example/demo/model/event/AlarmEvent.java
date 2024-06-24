package com.example.demo.model.event;

import com.example.demo.model.entity.AlarmArgs;
import com.example.demo.model.entity.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmEvent {
    private Integer receiveUserId;
    private AlarmType alarmType;
    private AlarmArgs args;
}
