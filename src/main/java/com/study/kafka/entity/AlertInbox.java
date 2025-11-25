package com.study.kafka.entity;

import com.study.kafka.entity.common.BaseEntity;
import com.study.kafka.model.TrxEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Kafka 이벤트를 처리했는지 여부를 기록하는 멱등성 테이블
 * -> Kafka 재처리/중복 소비 시 알림 중복 발송 방지
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlertInbox extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertInboxId;
    private Long accountId;
    // 비즈니스 멱등키
    private String eventId;
    private String topic;
    private Long partitionNo;
    private Long offsetNo;
    private LocalDateTime receivedAt;
}
