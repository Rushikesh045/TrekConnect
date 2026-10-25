package com.trekconnect.core.repository;

import com.trekconnect.core.entity.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, String> {
    List<EmailLog> findByRecipientEmailOrderByCreatedAtDesc(String recipientEmail);
    List<EmailLog> findByType(String type);
}
