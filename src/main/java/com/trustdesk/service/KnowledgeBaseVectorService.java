package com.trustdesk.service;

import com.trustdesk.entity.KnowledgeDocument;
import com.trustdesk.repository.KnowledgeDocumentRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;

import java.util.List;
import java.util.Map;

@Service
public class KnowledgeBaseVectorService {

    private final VectorStore vectorStore;
    private final KnowledgeDocumentRepository knowledgeDocumentRepository;

    public KnowledgeBaseVectorService(
            VectorStore vectorStore,
            KnowledgeDocumentRepository knowledgeDocumentRepository) {

        this.vectorStore = vectorStore;
        this.knowledgeDocumentRepository = knowledgeDocumentRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeVectorStore() {
        indexKnowledgeBaseWithChunks();
    }

    public void indexKnowledgeBase() {

        List<KnowledgeDocument> documents =
                knowledgeDocumentRepository.findAll();

        List<Document> vectorDocuments = documents.stream()
                .map(this::toVectorDocument)
                .toList();

        if (!vectorDocuments.isEmpty()) {
            vectorStore.add(vectorDocuments);
        }
    }
    public void indexKnowledgeBaseWithChunks() {

        List<KnowledgeDocument> documents =
                knowledgeDocumentRepository.findAll();

        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(800)
                .withMinChunkSizeChars(350)
                .withMinChunkLengthToEmbed(10)
                .withMaxNumChunks(5000)
                .withKeepSeparator(true)
                .build();

        for (KnowledgeDocument knowledgeDocument : documents) {

            Document document = new Document(
                    knowledgeDocument.getContent(),
                    Map.of(
                            "docId", knowledgeDocument.getDocId(),
                            "title", knowledgeDocument.getTitle(),
                            "sourcePath", knowledgeDocument.getSourcePath()
                    )
            );

            List<Document> chunks =
                    splitter.apply(List.of(document));

            vectorStore.add(chunks);
        }
    }

    private Document toVectorDocument(
            KnowledgeDocument knowledgeDocument) {

        return new Document(
                knowledgeDocument.getContent(),
                java.util.Map.of(
                        "docId", knowledgeDocument.getDocId(),
                        "title", knowledgeDocument.getTitle(),
                        "sourcePath", knowledgeDocument.getSourcePath()
                )
        );
    }
}