package com.study.kafka.common.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Kafka 인프라 레벨 설정
 */
@Configuration
@RequiredArgsConstructor
public class KafkaAdminConfig {
    private final KafkaProperties kafkaProperties;
    private final SslBundles sslBundles;

    @Bean(destroyMethod = "close")
    public AdminClient adminClient() {
        Map<String, Object> configs =
                kafkaProperties.buildAdminProperties(sslBundles);
        return AdminClient.create(configs);
    }
}
