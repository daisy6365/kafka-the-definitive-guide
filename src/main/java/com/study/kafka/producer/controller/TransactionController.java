package com.study.kafka.producer.controller;

import com.study.kafka.producer.model.TrxRequest;
import com.study.kafka.producer.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody TrxRequest request){
        transactionService.create(request);
        return ResponseEntity.ok().build();
    }
}
