package com.gznznzjsn.orderservice.web.kafka;

import com.gznznzjsn.orderservice.web.kafka.parser.XMLParser;
import com.jcabi.xml.XML;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class TaskProducerConfig {

    private final XML taskProducerSettings;

    @Bean
    public NewTopic tasksTopic() {
        return TopicBuilder.name("tasks")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public SenderOptions<String, String> senderOptions() {
        XMLParser parser = new XMLParser(taskProducerSettings);
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                parser.parse("bootstrapServers"));
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                parser.parse("keySerializerClass"));
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                parser.parse("valueSerializerClass"));
        return SenderOptions.create(properties);
    }

    @Bean
    public KafkaSender<String, String> sender(SenderOptions<String, String> senderOptions) {
        return KafkaSender.create(senderOptions);
    }

}
