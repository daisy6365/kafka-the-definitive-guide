package com.study.kafka.repository;

import com.study.kafka.entity.AlertInbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertInboxRepository extends JpaRepository<AlertInbox, Long> {
}
