package com.gznznzjsn.orderservice.web.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@RequiredArgsConstructor
@Component
public class TaskSender {

    private final KafkaSender<String, Long> sender;

    public void send(Long taskId) {
        this.sender.send(
                Mono.just(
                        SenderRecord.create(
                                "tasks",
                                0,
                                System.currentTimeMillis(),
                                "task",
                                taskId,
                                null
                        )
                )
        ).subscribe();
    }

}
