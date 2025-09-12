package com.study.kafka.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlertPolicy extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertPolicyId;
    private Long accountId;
    private BigDecimal thresholdAmount;
    private String currency;
    private YN isPush;
    private YN isSMS;
    private LocalTime alertTimeStart;
    private LocalTime alertTimeEnd;
}
