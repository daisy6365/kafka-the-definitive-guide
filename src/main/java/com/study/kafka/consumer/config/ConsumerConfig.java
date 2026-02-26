package com.study.kafka.consumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.kafka.consumer.model.TrxConsumerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
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
        factory.setConcurrency(3);
        // Retry + DLT 적용
        factory.setCommonErrorHandler(defaultErrorHandler);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        /**
         * Rebalance 이벤트 등록
         * Consumer가 작동 중단 or 추가 될 때,
         * Rebalance를 통해 Consumer에게 Partition을 재분배
         */
        factory.getContainerProperties().setConsumerRebalanceListener(new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> collection) {
                // partition을 반납
                log.info("[REBALANCE] revoked = {}", collection);

            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                // 재할당 뒤 재시작
                log.info("[REBALANCE] assigned = {}", collection);

            }
        });

        return factory;
    }
}
