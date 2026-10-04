package com.trustdesk.repository;

import com.trustdesk.entity.AiTrace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiTraceRepository extends JpaRepository<AiTrace, Long> {

    List<AiTrace> findByTicketIdOrderByCreatedAtDesc(String ticketId);
}