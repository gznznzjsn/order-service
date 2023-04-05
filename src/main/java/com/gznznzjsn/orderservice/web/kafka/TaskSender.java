package com.gznznzjsn.orderservice.web.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Component
@RequiredArgsConstructor
public class TaskSender {

    private final KafkaSender<String, String> sender;

    public void send(String taskId) {
        this.sender.send(
                Mono.just(
                        SenderRecord.create(
                                "tasks",
                                0,
                                System.currentTimeMillis(),
                                taskId,
                                taskId,
                                null
                        )
                )
        ).subscribe();
    }

}
