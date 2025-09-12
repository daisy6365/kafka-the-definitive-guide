package com.study.kafka.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlertLog extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertLogId;
    private Long accountId;
    private Long alertInboxId;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime trxDate;
    private String trxChannel;
    private Status status;
    private String errorCode;
    private String errorMessage;

}
