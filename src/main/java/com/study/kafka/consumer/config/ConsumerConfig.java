package com.study.kafka.consumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.kafka.consumer.model.TrxConsumerEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapServer;
    private final ObjectMapper objectMapper;

    @Bean
    public ConsumerFactory<String, TrxConsumerEvent> trxEventConsumerFactory(){
        JsonDeserializer<TrxConsumerEvent> jsonDeserializer = new JsonDeserializer<>(TrxConsumerEvent.class, objectMapper, false);
        jsonDeserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServer);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, "alert-service");
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
    }

    // (@Qualifier("trxEventConsumerFactory") -> 내가 설정한 trxEventConsumerFactory가 주입되도록 함

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TrxConsumerEvent> kafkaListenerContainerFactory(@Qualifier("trxEventConsumerFactory") ConsumerFactory<String, TrxConsumerEvent> consumerFactory,
                                                                                                           DefaultErrorHandler defaultErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, TrxConsumerEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setConcurrency(3);
        // Retry + DLT 적용
        factory.setCommonErrorHandler(defaultErrorHandler);

        return factory;
    }
}
