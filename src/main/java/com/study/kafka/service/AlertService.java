package com.study.kafka.service;

import com.study.kafka.entity.Account;
import com.study.kafka.entity.AlertLog;
import com.study.kafka.entity.AlertPolicy;
import com.study.kafka.entity.AlertPref;
import com.study.kafka.entity.common.Status;
import com.study.kafka.model.TrxEvent;
import com.study.kafka.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AlertService {
    private final StringRedisTemplate redisTemplate;

    private final AccountRepository accountRepository;
    private final AlertLogRepository alertLogRepository;
    private final AlertPolicyRepository alertPolicyRepository;
    private final AlertPrefRepository alertPrefRepository;

    private static final String KEY_PREFIX = "alert:idem:";

    /**
     * 1) kafka에서 event 수신받음
     * 2) 비즈니스로직 작성
     * 3) AlertInbox 고객에게 보여줄 알림함
     * 4) 알림 발송
     * 5) 알림 로그 저장
     * @param event
     */
    public void sendAlert(TrxEvent event){
        String eventId = event.getEventId();
        String redisKey = KEY_PREFIX + eventId;
        Long accountId = event.getAccountId();

        // 멱등성 체크
        if(Boolean.FALSE.equals(isIdempotented(redisKey))){
            log.info("[FAILED] 멱등성 체크 통과 실패. 중복 이벤트 = {}", eventId);
            // todo: error코드 구조 구현
//            saveFailLog()
            return;
        }

        // 계좌 조회
        Account account = accountRepository.findById(accountId)
                .orElse(null);
        if(account == null){
            log.info("[FAILED] 알림처리 불가 -> 해당 계좌 존재하지 않음. account = {}", accountId);
            return;
        }

        // 정책 조회
        AlertPolicy alertPolicy = alertPolicyRepository.findByAccountId(accountId);
        if(alertPolicy == null){
            log.info("[FAILED] 알림처리 불가 -> 정책 미존재. account = {}", accountId);
            return;
        }
        if(Boolean.FALSE.equals(checkPolicy(event, alertPolicy))){
            return;
        }

        // 연락수단 조회
        AlertPref alertPref = alertPrefRepository.findByAccountId(accountId);
        if(alertPref == null){
            log.info("[FAILED] 알림처리 불가 -> 연락수단 미존재. account = {}", accountId);
            return;
        }

        // 알림 발송
        firebaseFCM(event);

        // 알림 로그 저장
        saveAlertLog(event);

        log.info("[SUCCESS] 알림처리 완료. eventId = {} ", eventId);
    }



    private Boolean isIdempotented(String redisKey){
        return redisTemplate.opsForValue()
                .setIfAbsent(redisKey, "1", 24, TimeUnit.HOURS);
    }


    private Boolean checkPolicy(TrxEvent event, AlertPolicy alertPolicy){
        // 정책 1 - 임계금액 체크
        if(event.getAmount().compareTo(alertPolicy.getThresholdAmount()) < 0){
            log.info("[FAILED] 알림처리 불가 -> 임계 금액 미달. account = {}", alertPolicy.getAccountId());
            return Boolean.FALSE;
        }

        // 정책 2- 알림허용 시간대 체크
        if(!isAllowedTime(alertPolicy)){
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    private boolean isAllowedTime(AlertPolicy alertPolicy) {
        if(alertPolicy.getAlertTimeStart() == null) return true;
        if(alertPolicy.getAlertTimeEnd() == null) return true;

        LocalTime now = LocalTime.now();
        LocalTime alertTimeStart = alertPolicy.getAlertTimeStart();
        LocalTime alertTimeEnd = alertPolicy.getAlertTimeEnd();

        // 현재 시각이 알림허용시작 보다 이후, 알림허용종료 보다 이전
        return !now.isBefore(alertTimeStart) && now.isAfter(alertTimeEnd);
    }

    // 예시
    private void firebaseFCM(TrxEvent event){}

    private void saveAlertLog(TrxEvent event) {
        AlertLog alertLog = AlertLog.create(event.getAccountId(), event.getAmount(), event.getCurrency(),
                LocalDateTime.from(event.getTimestamp()), Status.SUCCESS);

        alertLogRepository.save(alertLog);
    }

    private void saveFailLog(TrxEvent event, String errorCode, String errorMessage){
        AlertLog alertLog = AlertLog.createFailed(event.getAccountId(), event.getAmount(), event.getCurrency(),
                LocalDateTime.from(event.getTimestamp()), Status.FAILURE, errorCode, errorMessage);
        alertLogRepository.save(alertLog);
    }
}
