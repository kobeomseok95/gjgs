package com.batch.redisbatch.redis;


import com.batch.redisbatch.service.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpirationListener implements MessageListener {

    private final OrderService orderService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String[] split = message.toString().split(",");
        Long teamId = Long.parseLong(split[0]);
        Long scheduleId = Long.parseLong(split[1]);
        orderService.changeStatus(teamId, scheduleId);
    }
}