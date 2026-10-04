package com.trustdesk.repository;

import com.trustdesk.entity.KnowledgeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KnowledgeDocumentRepository
        extends JpaRepository<KnowledgeDocument, String> {
}