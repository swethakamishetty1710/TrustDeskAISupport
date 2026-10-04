package com.trustdesk.service;

import com.trustdesk.entity.KnowledgeDocument;
import com.trustdesk.repository.KnowledgeDocumentRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class KnowledgeSearchService {

    private final KnowledgeDocumentRepository repository;
    private final VectorStore vectorStore;

    public KnowledgeSearchService(
            KnowledgeDocumentRepository repository,
            VectorStore vectorStore) {

        this.repository = repository;
        this.vectorStore = vectorStore;
    }

    // =========================================================
    // EXISTING KEYWORD SEARCH
    // KEEPING THIS AS BACKUP
    // =========================================================

    public List<KnowledgeDocument> search(String query) {

        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }

        String[] keywords = query
                .toLowerCase()
                .split("\\W+");

        List<KnowledgeDocument> documents = repository.findAll();

        Map<KnowledgeDocument, Integer> scores = new HashMap<>();

        for (KnowledgeDocument document : documents) {

            String text = (
                    document.getTitle() + " "
                            + document.getContent()
            ).toLowerCase();

            int score = 0;

            for (String keyword : keywords) {

                if (keyword.length() < 3) {
                    continue;
                }

                if (text.contains(keyword)) {
                    score++;
                }
            }

            if (score > 0) {
                scores.put(document, score);
            }
        }

        return scores.entrySet()
                .stream()
                .sorted(
                        Map.Entry.<KnowledgeDocument, Integer>
                                        comparingByValue()
                                .reversed()
                )
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


    // =========================================================
    // NEW VECTOR / SEMANTIC SEARCH
    // =========================================================

    public List<KnowledgeDocument> vectorSearch(String query) {

        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }

        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(5)
                        .build()
        );

        return results.stream()
                .map(this::findKnowledgeDocument)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    // =========================================================
    // FIND ORIGINAL DOCUMENT FROM MYSQL
    // =========================================================

    private KnowledgeDocument findKnowledgeDocument(
            Document document) {

        Object docId = document.getMetadata().get("docId");

        if (docId == null) {
            return null;
        }

        return repository.findById(docId.toString())
                .orElse(null);
    }
    public List<Document> vectorSearchWithScores(String query) {

        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }

        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(5)
                        .build()
        );
    }
    public List<KnowledgeSearchResult> vectorSearchWithResults(String query) {

        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }

        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(5)
                        .build()
        );

        return results.stream()
                .map(document -> {
                    KnowledgeDocument knowledgeDocument =
                            findKnowledgeDocument(document);

                    if (knowledgeDocument == null) {
                        return null;
                    }

                    Double score = null;

                    Object scoreValue =
                            document.getMetadata().get("score");

                    if (scoreValue instanceof Number number) {
                        score = number.doubleValue();
                    }

                    return new KnowledgeSearchResult(
                            knowledgeDocument,
                            score
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }
}