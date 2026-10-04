package com.trustdesk.controller;

import com.trustdesk.service.KnowledgeBaseVectorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/knowledge")
public class VectorIndexController {

    private final KnowledgeBaseVectorService vectorService;

    public VectorIndexController(
            KnowledgeBaseVectorService vectorService) {
        this.vectorService = vectorService;
    }

    @PostMapping("/vector-index/chunks")
    public String indexKnowledgeBaseWithChunks() {

        vectorService.indexKnowledgeBaseWithChunks();

        return "Knowledge base indexed using chunks";
    }
}