package com.study.kafka.producer;

import com.study.kafka.model.TrxEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrxEventProducer {
    private final KafkaTemplate<String, TrxEvent> kafkaTemplate;

    // topic 이름은 property로 가져옴
    @Value("${kafka.topic}")
    private String topic;

    public void send(String key, TrxEvent trxEvent) {
        kafkaTemplate.send(topic, key, trxEvent)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("produce failed. topic={} key={} event={} err={}", topic, key, trxEvent, ex.toString());
                    }
                    else {
                        RecordMetadata metadata = result.getRecordMetadata();
                        log.info("produced. {}-{} offset={} key={}",  metadata.topic(), metadata.partition(), metadata.offset(), key);
                    }
                });
    }
}
