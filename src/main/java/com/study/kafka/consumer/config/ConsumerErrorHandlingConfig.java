package com.study.kafka.consumer.config;

import com.study.kafka.consumer.model.TrxConsumerEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * 메세지 처리 중 예외 발생 시, 재시도 + DLT 동작 설정
 * 1) backoff 포함 N번 재시도
 * 2) DLT 토픽으로 publish
 */
@Slf4j
@Configuration
public class ConsumerErrorHandlingConfig {
    /**
     *
     * @param kafkaTemplate
     * -> Consumer에서 설정한 타입과 동일하게 설정
     */
    @Bean
    public DefaultErrorHandler defaultErrorHandler(@Qualifier("dltKafkaTemplate") KafkaTemplate<String, TrxConsumerEvent> kafkaTemplate) {
        // Recoverer 객체 : 실패 메세지를 DLT로 보냄
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate, ((consumerRecord, e) ->
                new TopicPartition(consumerRecord.topic() + ".DLT", consumerRecord.partition()))
        );

        // retry 정책 : 실패 후 1초 간격으로 3번 재시도
        FixedBackOff backOff = new FixedBackOff(1000L, 3L);

        return getDefaultErrorHandler(recoverer, backOff);
    }

    private static DefaultErrorHandler getDefaultErrorHandler(DeadLetterPublishingRecoverer recoverer, FixedBackOff backOff) {
        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);

        // [Option] 특정 예외에 따라 재시도 하지 않고 바로 DLT 보냄
//        handler.addNotRetryableExceptions(BizException.class);

        handler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.info("[RETRY] attempt = [{}], topic = [{}], partition = [{}], offset = [{}], key = [{}], error = [{}]",
                    deliveryAttempt, record.topic(), record.partition(), record.offset(), record.key(), ex.getClass().getSimpleName());
        });
        return handler;
    }

}
