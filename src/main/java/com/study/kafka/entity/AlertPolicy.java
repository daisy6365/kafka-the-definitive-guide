package com.study.kafka.entity;

import com.study.kafka.entity.common.BaseEntity;
import com.study.kafka.entity.common.YN;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlertPolicy extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertPolicyId;
    private Long accountId;
    private BigDecimal thresholdAmount = BigDecimal.ZERO;
    private String currency;
    @Enumerated(EnumType.STRING)
    private YN isPush;
    @Enumerated(EnumType.STRING)
    private YN isSMS;
    private LocalTime alertTimeStart;
    private LocalTime alertTimeEnd;
}
