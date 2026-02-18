package com.study.kafka.producer.service;

import com.study.kafka.common.exception.BizException;
import com.study.kafka.common.exception.ErrorCode;
import com.study.kafka.common.type.TrxType;
import com.study.kafka.common.util.IdGeneratorUtil;
import com.study.kafka.entity.Account;
import com.study.kafka.producer.TrxEventProducer;
import com.study.kafka.producer.model.TrxProducerEvent;
import com.study.kafka.producer.model.TrxRequest;
import com.study.kafka.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

import static com.study.kafka.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TrxEventProducer trxEventProducer;
    private final AccountRepository accountRepository;

    @Transactional
    public void create(TrxRequest request){
        // 계좌 검증
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new BizException(ACCOUNT_NOT_FOUND));

        // 금액 검증
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException(INVALID_AMOUNT);
        }

        // 계좌 잔액 update
        BigDecimal balance = BigDecimal.ZERO;
        if(request.getType() == TrxType.CREDIT){
            // 잔액 감소
            if(account.getBalance().compareTo(request.getAmount()) < 0){
                throw new BizException(INSUFFICIENT_BALANCE);
            }
            balance = account.withdraw(request.getAmount());
        } else if (request.getType() == TrxType.DEBIT) {
            // 잔액 증가
            balance = account.deposit(request.getAmount());
        }
        else {
            throw new BizException(INVALID_TRANSACTION_TYPE);
        }
        accountRepository.save(account);

        // event 생성
        String eventId = IdGeneratorUtil.generateEventId();
        // 멱등성 테스트로 인해 동일 ID로 테스트
//        String eventId = "2024023932033699840";
        String trxId = IdGeneratorUtil.generateTrxId();
        TrxProducerEvent event = TrxProducerEvent.from(eventId, trxId, request.getAccountId(), request.getType(),
                request.getAmount(), request.getCurrency(), Instant.now(), request.getDescription(), balance);

        trxEventProducer.sendAsync(request.getAccountId(), event);
    }
}
