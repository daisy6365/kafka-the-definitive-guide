package com.study.kafka.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TrxRequest {
    private String accountId;
    private TrxType type;
    private BigDecimal amount;
    private String currency;
    private String description;
}
