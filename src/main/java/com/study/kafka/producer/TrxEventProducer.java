package com.study.kafka.producer;

import com.study.kafka.producer.model.TrxProducerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrxEventProducer {
    private final KafkaTemplate<String, TrxProducerEvent> kafkaTemplate;

    // topic 이름은 property로 가져옴
    @Value("${spring.kafka.topic}")
    private String topic;

    /**
     *  Spring Kafka의 .send()는 CompletableFuture<SendResult<K, V>>를 반환
     *  -> Kafka Plain Client 코드의 Callback구현 대신 lamda/method 레퍼런스로 처리하고자 함
     *
     *  즉, 동작의 의미는 같다.
     *  단지 Spring Callback을 CompletableFuture으로 감쌈
     */

    // 비동기 전송
    public void sendAsync(Long key, TrxProducerEvent trxEvent) {
        /**
         * Key : 고객 계좌정보
         * -> 고객 계좌정보를 기준으로 파티션 진행
         * **  상세 파티션 구현은 나중에 **
         */
        kafkaTemplate.send(topic, String.valueOf(key), trxEvent)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("ASYNC produce failed. topic={} key={} event={} err={}", topic, key, trxEvent, ex.toString());
                    }
                    else {
                        RecordMetadata metadata = result.getRecordMetadata();
                        log.info("ASYNC produced. eventId={} trxId={} topic={} partition={} offset={}",
                                trxEvent.getEventId(), trxEvent.getTrxId(), metadata.topic(), metadata.partition(), metadata.offset());
                        log.info("ASYNC produced. {}-{} offset={} key={}",  metadata.topic(), metadata.partition(), metadata.offset(), key);
                    }
                });
    }

    // 동기 전송
    public void sendSync(String key, TrxProducerEvent trxEvent) {
        try{
            SendResult<String, TrxProducerEvent> result = kafkaTemplate.send(topic, key, trxEvent).get(3, TimeUnit.SECONDS);
            RecordMetadata metadata = result.getRecordMetadata();
            log.info("SYNC produced. {}-{} offset={} key={}",  metadata.topic(), metadata.partition(), metadata.offset(), key);
        } catch (Exception e) {
            log.error("SYNC produce failed. topic={} key={}", topic, key, e);
            throw new IllegalStateException("Kafka produce failed", e);
        }
    }
}
