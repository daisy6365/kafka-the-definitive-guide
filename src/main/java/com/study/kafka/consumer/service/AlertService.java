package com.study.kafka.consumer.service;

import com.study.kafka.common.exception.BizException;
import com.study.kafka.consumer.model.AlertRequest;
import com.study.kafka.entity.Account;
import com.study.kafka.entity.AlertLog;
import com.study.kafka.entity.AlertPolicy;
import com.study.kafka.entity.AlertPref;
import com.study.kafka.entity.common.Status;
import com.study.kafka.consumer.model.TrxConsumerEvent;
import com.study.kafka.repository.AccountRepository;
import com.study.kafka.repository.AlertLogRepository;
import com.study.kafka.repository.AlertPolicyRepository;
import com.study.kafka.repository.AlertPrefRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

import static com.study.kafka.common.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AlertService {
    private static final String KEY_PREFIX = "alert:idem:";
    private final StringRedisTemplate redisTemplate;

    private final AccountRepository accountRepository;
    private final AlertLogRepository alertLogRepository;
    private final AlertPolicyRepository alertPolicyRepository;
    private final AlertPrefRepository alertPrefRepository;

    /**
     * 1) kafka에서 request 수신받음
     * 2) 비즈니스로직 작성
     * 3) AlertInbox 고객에게 보여줄 알림함
     * 4) 알림 발송
     * 5) 알림 로그 저장
     * @param request
     */
    public void sendAlert(AlertRequest request){
        String eventId = request.getEventId();
        String redisKey = KEY_PREFIX + eventId;
        Long accountId = request.getAccountId();

        // 멱등성 체크
        if(Boolean.FALSE.equals(isIdempotented(redisKey))){
            log.info("[FAILED] 멱등성 체크 통과 실패. 중복 이벤트 = {}", eventId);
            throw new BizException(IDEMPOTENCY_VIOLATION);
        }

        // 계좌 조회
        Account account = accountRepository.findById(accountId)
                .orElse(null);
        if(account == null){
            log.info("[FAILED] 알림처리 불가 -> 해당 계좌 존재하지 않음. account = {}", accountId);
            throw new BizException(ACCOUNT_NOT_FOUND);
        }

        // 정책 조회
        AlertPolicy alertPolicy = alertPolicyRepository.findByAccountId(accountId);
        if(alertPolicy == null){
            log.info("[FAILED] 알림처리 불가 -> 정책 미존재. account = {}", accountId);
            throw new BizException(POLICY_NOT_FOUND);
        }
        checkPolicy(request, alertPolicy);

        // 연락수단 조회
        AlertPref alertPref = alertPrefRepository.findByAccountId(accountId);
        if(alertPref == null){
            log.info("[FAILED] 알림처리 불가 -> 연락수단 미존재. account = {}", accountId);
            throw new BizException(PREF_NOT_FOUND);
        }

        // 알림 발송
        firebaseFCM(request);
        // 알림 로그 저장
        saveAlertLog(request);
        log.info("[SUCCESS] 알림처리 완료. eventId = {} ", eventId);
    }

    private Boolean isIdempotented(String redisKey){
        // 24시간 이내에 같은 redisKey가 들어오면 중복으로 인지 -> false 반환
        // setIfAbsent으로 멱등토큰획득
        return redisTemplate.opsForValue()
                .setIfAbsent(redisKey, "1", 24, TimeUnit.HOURS);
    }

    private void checkPolicy(AlertRequest request, AlertPolicy alertPolicy){
        // 정책 1 - 임계금액 체크
        if(request.getAmount().compareTo(alertPolicy.getThresholdAmount()) < 0){
            log.info("[FAILED] 알림처리 불가 -> 임계 금액 미달. account = {}", alertPolicy.getAccountId());
            throw new BizException(POLICY_THRESHOLD_NOT_MET);
        }

        // 정책 2- 알림허용 시간대 체크
        if(!isAllowedTime(alertPolicy)){
            log.info("[FAILED] 알림처리 불가 -> 알림허용 시간대 아님. account = {}", alertPolicy.getAccountId());
            throw new BizException(POLICY_TIME_NOT_ALLOWED);
        }
    }

    private boolean isAllowedTime(AlertPolicy alertPolicy) {
        if(alertPolicy.getAlertTimeStart() == null) return true;
        if(alertPolicy.getAlertTimeEnd() == null) return true;

        LocalTime now = LocalTime.now();
        LocalTime alertTimeStart = alertPolicy.getAlertTimeStart();
        LocalTime alertTimeEnd = alertPolicy.getAlertTimeEnd();

        // 현재 시각이 알림허용시작 보다 이후, 알림허용종료 보다 이전
        return !now.isBefore(alertTimeStart) && !now.isAfter(alertTimeEnd);
    }

    // 예시
    private void firebaseFCM(AlertRequest request){}

    private void saveAlertLog(AlertRequest request) {
        AlertLog alertLog = AlertLog.create(request.getAccountId(), request.getType(), request.getAmount(), request.getCurrency(),
                LocalDateTime.ofInstant(request.getTimestamp(), ZoneId.systemDefault()), Status.SUCCESS);

        alertLogRepository.save(alertLog);
    }

    public void saveFailLog(AlertRequest request, String errorCode, String errorMessage){
        AlertLog alertLog = AlertLog.createFailed(request.getAccountId(), request.getType(), request.getAmount(), request.getCurrency(),
                LocalDateTime.ofInstant(request.getTimestamp(), ZoneId.systemDefault()), Status.FAILURE, errorCode, errorMessage);
        alertLogRepository.save(alertLog);
    }
}
