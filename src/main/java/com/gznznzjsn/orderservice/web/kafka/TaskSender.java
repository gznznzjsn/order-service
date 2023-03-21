package com.gznznzjsn.orderservice.web.kafka;

import com.gznznzjsn.orderservice.web.kafka.parser.XMLParser;
import com.jcabi.xml.XML;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Component
@RequiredArgsConstructor
public class TaskSender {

    private final XML taskProducerSettings;
    private final KafkaSender<String, String> sender;

    public void send(String taskId) {
        this.sender.send(
                Mono.just(
                        SenderRecord.create(
                                "tasks",
                                0,
                                System.currentTimeMillis(),
                                new XMLParser(taskProducerSettings).parse("requiredTaskKey"),
                                taskId,
                                null
                        )
                )
        ).subscribe();
    }

}
