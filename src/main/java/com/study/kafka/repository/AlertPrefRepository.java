package com.study.kafka.repository;

import com.study.kafka.entity.AlertPref;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertPrefRepository extends JpaRepository<AlertPref, Long> {
}
