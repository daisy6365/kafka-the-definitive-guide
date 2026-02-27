package com.study.kafka.consumer.model;

import com.study.kafka.common.type.TrxType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@NoArgsConstructor
public class TrxConsumerEvent {
    private String eventId; // kafka 이벤트 식별자 -> 멱등성, 재처리 방지
    private String trxId; // 거래 ID
    private Long accountId; // 계좌 ID
    private TrxType type; // 차대변 구분 (입출금이라고도 함)
    private BigDecimal amount; // 거래 금액
    private String currency; // 통화 코드
    private Instant timestamp; // 거래 timestamp
    private String description; // 설명
    private BigDecimal balance; // 거래 후 잔액
    private Integer sequence; // partition 순서
}
