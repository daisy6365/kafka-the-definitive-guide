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
}
