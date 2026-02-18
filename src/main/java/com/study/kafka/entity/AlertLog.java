package com.study.kafka.entity;

import com.study.kafka.common.type.TrxType;
import com.study.kafka.entity.common.BaseEntity;
import com.study.kafka.entity.common.Channel;
import com.study.kafka.entity.common.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 알림 발송 기록
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlertLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertLogId;
    private Long accountId;
    @Enumerated(EnumType.STRING)
    private TrxType trxType;
    private BigDecimal amount = BigDecimal.ZERO;
    private String currency;
    private LocalDateTime trxDate;
    @Enumerated(EnumType.STRING)
    private Channel trxChannel;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String errorCode;
    private String errorMessage;

    public static AlertLog create(Long accountId, TrxType trxType, BigDecimal amount, String currency,
                                         LocalDateTime trxDate, Status status) {
        AlertLog alertLog = new AlertLog();

        alertLog.accountId = accountId;
        alertLog.trxType = trxType;
        alertLog.amount = amount;
        alertLog.currency = currency;
        alertLog.trxDate = trxDate;
        alertLog.trxChannel = Channel.PUSH;
        alertLog.status = status;

        return alertLog;
    }

    public static AlertLog createFailed(Long accountId, TrxType trxType, BigDecimal amount, String currency,
                                 LocalDateTime trxDate, Status status, String errorCode, String errorMessage) {
        AlertLog alertLog = create(accountId, trxType, amount, currency, trxDate, status);

        alertLog.errorCode = errorCode;
        alertLog.errorMessage = errorMessage;

        return alertLog;
    }
}
