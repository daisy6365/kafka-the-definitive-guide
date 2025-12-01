package com.study.kafka.repository;

import com.study.kafka.entity.AlertPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertPolicyRepository extends JpaRepository<AlertPolicy, Long> {
    AlertPolicy findByAccountId(Long accountId);
}
