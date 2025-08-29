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
    private String trxId;
    private String accountId;
    private TrxType type;
    private BigDecimal amount;
    private String currency;
    private Instant timestamp;
    private String description;

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
