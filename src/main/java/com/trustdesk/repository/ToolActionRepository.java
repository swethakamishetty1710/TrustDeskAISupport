package com.trustdesk.repository;

import com.trustdesk.entity.ToolAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToolActionRepository extends JpaRepository<ToolAction, Long> {

    Optional<ToolAction> findByIdempotencyKey(String idempotencyKey);
}