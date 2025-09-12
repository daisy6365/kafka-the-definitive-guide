package com.study.kafka.entity;

import com.study.kafka.entity.common.AccountStatus;
import com.study.kafka.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;
    private Long customerId;
    private String accountNo;
    private BigDecimal balance;
    private String currency;
    private String accountType;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private LocalDate openDate;

}
