package com.study.kafka.common.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.admin.ListOffsetsResult;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class KafkaLagService {
    private final AdminClient adminClient;

    public Map<TopicPartition, Long> getLag(String groupId) throws ExecutionException, InterruptedException {
        Map<TopicPartition, Long> lagMap = new HashMap<>();

        // 현재 Consumer가 commit한 offset 정보를 가져옴
        ListConsumerGroupOffsetsResult offsetsResult = adminClient.listConsumerGroupOffsets(groupId);
        Map<TopicPartition, OffsetAndMetadata> consumerOffsets = offsetsResult.partitionsToOffsetAndMetadata().get();

        // 각 partition의 최신 offset 조회
        Map<TopicPartition, OffsetSpec> requestLatestOffsets = new HashMap<>();
        for (TopicPartition tp : consumerOffsets.keySet()) {
            requestLatestOffsets.put(tp, OffsetSpec.latest());
        }

        ListOffsetsResult latestOffsetsResult = adminClient.listOffsets(requestLatestOffsets);
        for (TopicPartition tp : consumerOffsets.keySet()) {
            // consumer가 처리한 offset
            long consumerOffset = consumerOffsets.get(tp).offset();
            // 현재까지 쌓인 최신 offset
            long latestOffset = latestOffsetsResult.partitionResult(tp).get().offset();
            long lag = latestOffset - consumerOffset;

            // lag은 topic 전체가 아니라 각 partition 마다 존재
            lagMap.put(tp, lag);
        }
        return lagMap;
    }

}
