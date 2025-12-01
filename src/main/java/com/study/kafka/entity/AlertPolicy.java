package com.study.kafka.entity;

import com.study.kafka.entity.common.BaseEntity;
import com.study.kafka.entity.common.YN;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * 계좌별 알림기준을 저장하는 정책 테이블
 * - 임계치
 * - 허용 시간대등
 */
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
    private LocalTime alertTimeStart;
    private LocalTime alertTimeEnd;
}
