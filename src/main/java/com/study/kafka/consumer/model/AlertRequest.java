package com.study.kafka.consumer.model;

import com.study.kafka.common.type.TrxType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@NoArgsConstructor
public class AlertRequest {
    private String eventId;
    private String trxId;
    private Long accountId;
    private TrxType type;
    private BigDecimal amount;
    private String currency;
    private Instant timestamp;
    private String description;
    private BigDecimal balance;

    public static AlertRequest from(TrxConsumerEvent event){
        AlertRequest request = new AlertRequest();
        request.eventId = event.getEventId();
        request.trxId = event.getTrxId();
        request.accountId = event.getAccountId();
        request.type = event.getType();
        request.amount = event.getAmount();
        request.currency = event.getCurrency();
        request.timestamp = event.getTimestamp();
        request.description = event.getDescription();
        request.balance = event.getBalance();
        return request;
    }
}