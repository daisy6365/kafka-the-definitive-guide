package com.study.kafka.producer.model;

import com.study.kafka.common.type.TrxType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@NoArgsConstructor
public class TrxProducerEvent {
    private String eventId; // kafka 이벤트 식별자 -> 멱등성, 재처리 방지
    private String trxId; // 거래 ID
    private Long accountId; // 계좌 ID
    private TrxType type; // 차대변 구분 (입출금이라고도 함)
    private BigDecimal amount; // 거래 금액
    private String currency; // 통화 코드
    private Instant timestamp; // 거래 timestamp
    private String description; // 설명
    private BigDecimal balance = BigDecimal.ZERO; // 거래 후 잔액
    // Partition 처리 순서 확인
    private Integer sequence;

    public static TrxProducerEvent from(String eventId, String trxId, Long accountId, TrxType type, BigDecimal amount,
                                        String currency, Instant timestamp, String description, BigDecimal balance, Integer sequence) {
        TrxProducerEvent event = new TrxProducerEvent();
        event.eventId = eventId;
        event.trxId = trxId;
        event.accountId = accountId;
        event.type = type;
        event.amount = amount;
        event.currency = currency;
        event.timestamp = timestamp;
        event.description = description;
        event.balance = balance;
        event.sequence = sequence;
        return event;
    }
}
