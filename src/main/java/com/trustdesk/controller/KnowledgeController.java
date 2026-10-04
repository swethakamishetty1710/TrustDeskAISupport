package com.trustdesk.controller;

import com.trustdesk.entity.KnowledgeDocument;
import com.trustdesk.repository.KnowledgeDocumentRepository;
import com.trustdesk.service.KnowledgeSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final KnowledgeSearchService knowledgeSearchService;
    private final KnowledgeDocumentRepository knowledgeDocumentRepository;

    public KnowledgeController(
            KnowledgeSearchService knowledgeSearchService,
            KnowledgeDocumentRepository knowledgeDocumentRepository) {

        this.knowledgeSearchService = knowledgeSearchService;
        this.knowledgeDocumentRepository = knowledgeDocumentRepository;
    }

    // Existing keyword search - kept as backup
    @GetMapping("/search")
    public List<KnowledgeDocument> search(
            @RequestParam String query) {

        return knowledgeSearchService.search(query);
    }

    // Get a specific knowledge document by citation ID
    @GetMapping("/{docId}")
    public ResponseEntity<KnowledgeDocument> getKnowledgeDocument(
            @PathVariable String docId) {

        return knowledgeDocumentRepository.findById(docId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}