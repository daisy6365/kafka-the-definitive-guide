package com.study.kafka.consumer;

import com.study.kafka.common.exception.BizException;
import com.study.kafka.consumer.model.AlertRequest;
import com.study.kafka.consumer.model.TrxConsumerEvent;
import com.study.kafka.consumer.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertListener {
    private final AlertService alertService;

    /**
     * 구독 (1)
     * topic 이름으로 구독
     */
    @KafkaListener(
            topics = "trx-created.v1",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "alert-service"
    )
    public void onMessage(@Payload TrxConsumerEvent event,
                          @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                          @Header(KafkaHeaders.OFFSET) long offset,
                          Acknowledgment ack) {
        // send sms/push
        log.info("Consumed eventId={} partition={} offset={}", event.getEventId(), partition, offset);
        try {
            alertService.sendAlert(AlertRequest.from(event));
            ack.acknowledge();
        }
        catch (BizException e){
            alertService.saveFailLog(AlertRequest.from(event), e.getErrorCode().getCode(), e.getErrorCode().getMessage());
            ack.acknowledge();
        }
        catch (Exception e){
            log.error("[FATAL] Unexpected error while processing event {}", event, e);
        }
    }

    /**
     * 구독 (2)
     * 패턴으로 구독
     */
//    @KafkaListener(
//            topicPattern = "",
//            containerFactory = "kafkaListenerContainerFactory",
//            groupId = "alert-service"
//    )
//    public void onMessageByPattern(@Payload TrxConsumerEvent event,
//                                   Acknowledgment ack) {
//        ack.acknowledge();
//    }

    /**
     * 구독 (3)
     * 특정 파티션에 고정할당
     * -> 리밸런스 없이 직접 제어
     * -> 스케일링 자동화 기능 사라짐
     */
//    @KafkaListener(
//            topicPartitions = {
//                    @TopicPartition(topic = "txn-created.v1",
//                    partitions =  {"0", "1"})
//            },
//            containerFactory = "kafkaListenerContainerFactory",
//            groupId = "alert-service"
//    )
//    public void onMessageFixed(@Payload TrxConsumerEvent event,
//                               Acknowledgment ack) {
//        ack.acknowledge();
//    }
}
