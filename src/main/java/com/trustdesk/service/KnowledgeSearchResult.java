package com.trustdesk.service;

import com.trustdesk.entity.KnowledgeDocument;

public class KnowledgeSearchResult {

    private final KnowledgeDocument document;
    private final Double score;

    public KnowledgeSearchResult(
            KnowledgeDocument document,
            Double score) {
        this.document = document;
        this.score = score;
    }

    public KnowledgeDocument getDocument() {
        return document;
    }

    public Double getScore() {
        return score;
    }
}