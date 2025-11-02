package com.securefilex.securefilex.repository;

import com.securefilex.securefilex.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUsernameContainingIgnoreCase(String username);
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
