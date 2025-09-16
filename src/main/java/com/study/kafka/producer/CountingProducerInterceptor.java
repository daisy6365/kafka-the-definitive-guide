package com.study.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Producer Interceptor 예제
 * 전송된 message의 수와 특정한 시간 윈도우 사이에 broker가 리턴한 ack수 집계 class
 */
@Slf4j
public class CountingProducerInterceptor implements ProducerInterceptor {
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    static AtomicLong numSent = new AtomicLong(0);
    static AtomicLong numAcked = new AtomicLong(0);

    /**
     * 레코드를 broker로 보내기 전, 직렬화 되기 전에 호출
     * -> before 같은 개념
     */
    @Override
    public ProducerRecord onSend(ProducerRecord record) {
        // 전송에 대한 카운팅
        numSent.incrementAndGet();
        return record;
    }

    /**
     * Kafka broker가 보낸 응답을 client가 받았을 때 호출
     * -> after 같은 개념
     */
    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        // 응답에 대한 카운팅
        numAcked.incrementAndGet();
    }

    @Override
    public void close() {
        executorService.shutdown();
    }

    /**
     * class 초기화 시 설정값을 읽어옴
     * 주기마다 run()을 자동 실행하도록 스케줄러에 등록
     */
    @Override
    public void configure(Map<String, ?> configs) {
//        Long windowSize = Long.valueOf(
//                (String) configs.get("counting.interceptor.window.size.ms"));
        // 형변환에 취약하므로 안전하게 구현 -> instanceof
        Object raw = configs.get("counting.interceptor.window.size.ms");
        long windowSize = (raw instanceof Number)
                ? ((Number) raw).longValue()
                : Long.parseLong(String.valueOf(raw));
        executorService.scheduleAtFixedRate(CountingProducerInterceptor::run, windowSize, windowSize, TimeUnit.MILLISECONDS);
    }

    public static void run() {
        log.info("CountingProducerInterceptor, numSent = {}", numSent);
        log.info("CountingProducerInterceptor, numAcked = {}", numAcked);
    }
}
