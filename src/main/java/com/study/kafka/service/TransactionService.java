package com.study.kafka.service;

import com.study.kafka.model.TrxEvent;
import com.study.kafka.model.TrxRequest;
import com.study.kafka.producer.TrxEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TrxEventProducer trxEventProducer;

    public void create(TrxRequest request){
        String trxId = "";
        TrxEvent event = TrxEvent.from(trxId, request.getAccountId(), request.getType(), request.getAmount(), request.getCurrency(),
                Instant.now(), request.getDescription());

        trxEventProducer.sendAsync(request.getAccountId(), event);

    }
}
