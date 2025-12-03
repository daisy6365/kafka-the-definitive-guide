package com.study.kafka.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 멱등성 001~099
    IDEMPOTENCY_VIOLATION("001", "Duplicate event received"),

    // 계좌 101~199
    ACCOUNT_NOT_FOUND("101", "Account not found"),

    // 정책 201~299
    POLICY_NOT_FOUND("201", "Alert policy not registered"),
    POLICY_THRESHOLD_NOT_MET("202", "Threshold amount condition not met"),
    POLICY_TIME_NOT_ALLOWED("203", "Notification not allowed in current time window"),

    // 연락정보
    PREF_NOT_FOUND("301", "Alert preference not found");

    private final String code;
    private final String message;
}
