package com.study.kafka.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    IDEMPOTENCY_VIOLATION("001", "Duplicate event received"),

    ACCOUNT_NOT_FOUND("101", "Account not found"),
    INSUFFICIENT_BALANCE("102", "Insufficient balance"),
    INVALID_TRANSACTION_TYPE("103", "Unsupported transaction type"),
    INVALID_AMOUNT("104", "Amount must be greater than zero"),

    POLICY_NOT_FOUND("201", "Alert policy not registered"),
    POLICY_THRESHOLD_NOT_MET("202", "Threshold amount condition not met"),
    POLICY_TIME_NOT_ALLOWED("203", "Notification not allowed in current time window"),

    PREF_NOT_FOUND("301", "Alert preference not found");

    private final String code;
    private final String message;
}
