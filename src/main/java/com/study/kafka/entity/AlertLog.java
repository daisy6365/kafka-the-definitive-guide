package com.study.kafka.entity;

import com.study.kafka.entity.common.BaseEntity;
import com.study.kafka.entity.common.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 알림 발송 기록
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlertLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertLogId;
    private Long accountId;
    private Long alertInboxId;
    private BigDecimal amount = BigDecimal.ZERO;
    private String currency;
    private LocalDateTime trxDate;
    private String trxChannel;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String errorCode;
    private String errorMessage;

}
