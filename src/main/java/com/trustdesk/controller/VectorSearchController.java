package com.trustdesk.controller;

import com.trustdesk.entity.KnowledgeDocument;
import com.trustdesk.service.KnowledgeSearchResult;
import com.trustdesk.service.KnowledgeSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.ai.document.Document;

@RestController
@RequestMapping("/api/knowledge")
public class VectorSearchController {

    private final KnowledgeSearchService knowledgeSearchService;

    public VectorSearchController(
            KnowledgeSearchService knowledgeSearchService) {

        this.knowledgeSearchService = knowledgeSearchService;
    }

    @GetMapping("/vector-search")
    public List<KnowledgeDocument> vectorSearch(
            @RequestParam String query) {

        return knowledgeSearchService.vectorSearch(query);
    }
    @GetMapping("/vector-search-debug")
    public List<Document> vectorSearchDebug(
            @RequestParam String query) {

        return knowledgeSearchService.vectorSearchWithScores(query);
    }
    @GetMapping("/vector-search-results")
    public List<KnowledgeSearchResult> vectorSearchResults(
            @RequestParam String query) {

        return knowledgeSearchService.vectorSearchWithResults(query);
    }
}