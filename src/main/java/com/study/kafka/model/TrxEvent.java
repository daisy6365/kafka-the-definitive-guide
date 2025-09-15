package com.study.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrxEvent {
    private String eventId; // kafka 이벤트 식별자 -> 멱등성, 재처리 방지
    private String trxId; // 거래 ID
    private String accountId; // 계좌 ID
    private TrxType type; // 차대변 구분 (입출금이라고도 함)
    private BigDecimal amount; // 거래 금액
    private String currency; // 통화 코드
    private Instant timestamp; // 거래 timestamp
    private String description; // 설명
    private BigDecimal balance; // 거래 후 잔액

    public static TrxEvent from(String trxId, String accountId, TrxType type, BigDecimal amount, String currency, Instant timestamp, String description) {
        TrxEvent event = new TrxEvent();
        event.setTrxId(trxId);
        event.setAccountId(accountId);
        event.setType(type);
        event.setAmount(amount);
        event.setCurrency(currency);
        event.setTimestamp(timestamp);
        event.setDescription(description);
        return event;
    }
}
