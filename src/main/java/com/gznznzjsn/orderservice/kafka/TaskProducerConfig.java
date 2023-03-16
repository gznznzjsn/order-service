package com.gznznzjsn.orderservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TaskProducerConfig {

    @Bean
    public NewTopic tasksTopic() {
        return TopicBuilder.name("tasks")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public SenderOptions<String, Long> senderOptions() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        return SenderOptions.create(properties);
    }

    @Bean
    public KafkaSender<String, Long> sender(SenderOptions<String, Long> senderOptions) {
        return KafkaSender.create(senderOptions);
    }

}
