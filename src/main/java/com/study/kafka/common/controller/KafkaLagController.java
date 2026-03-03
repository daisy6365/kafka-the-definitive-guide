package com.study.kafka.common.controller;

import com.study.kafka.common.service.KafkaLagService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.TopicPartition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/kafka")
public class KafkaLagController {
    private final KafkaLagService kafkaLagService;

    @GetMapping("/lag")
    public Map<String, Long> getLag() throws ExecutionException, InterruptedException {
        Map<TopicPartition, Long> lagMap = kafkaLagService.getLag("alert-service");

        Map<String, Long> result = new HashMap<>();
        lagMap.forEach((tp, lag) -> {
            result.put(tp.topic() + "-" + tp.partition(), lag);
        });

        return result;
    }
}
