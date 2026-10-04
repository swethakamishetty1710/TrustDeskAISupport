package com.trustdesk.config;

import com.trustdesk.entity.KnowledgeDocument;
import com.trustdesk.repository.KnowledgeDocumentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;

@Component
public class KnowledgeBaseLoader implements CommandLineRunner {

    private final KnowledgeDocumentRepository repository;

    public KnowledgeBaseLoader(KnowledgeDocumentRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {

        loadDocument(
                "KB-ACCOUNT-001",
                "Account Security Policy",
                "data/knowledge_base/account_security_policy.md"
        );

        loadDocument(
                "KB-ADVERSARIAL-001",
                "Third-Party Widget Vendor Note",
                "data/knowledge_base/adversarial_vendor_note.md"
        );

        loadDocument(
                "KB-BILLING-001",
                "Billing and Payment Policy",
                "data/knowledge_base/billing_policy.md"
        );

        loadDocument(
                "KB-COUPON-001",
                "Coupon and Goodwill Policy",
                "data/knowledge_base/coupon_policy.md"
        );

        loadDocument(
                "KB-REFUND-001",
                "Refund and Return Policy",
                "data/knowledge_base/refund_policy.md"
        );

        loadDocument(
                "KB-SHIPPING-001",
                "Shipping and Delivery Policy",
                "data/knowledge_base/shipping_policy.md"
        );

        loadDocument(
                "KB-SECURITY-001",
                "AI Support Security Playbook",
                "data/knowledge_base/support_security_playbook.md"
        );

        loadDocument(
                "KB-WARRANTY-001",
                "Warranty and Product Safety Policy",
                "data/knowledge_base/warranty_policy.md"
        );

        System.out.println("========================================");
        System.out.println("Knowledge Base loading completed");
        System.out.println("========================================");
    }

    private void loadDocument(
            String docId,
            String title,
            String filePath) throws Exception {

        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Knowledge document not found: "
                    + file.getAbsolutePath());
            return;
        }

        if (repository.existsById(docId)) {
            System.out.println("Skipped existing document: " + docId);
            return;
        }

        String content = Files.readString(
                file.toPath(),
                StandardCharsets.UTF_8
        );

        KnowledgeDocument document = new KnowledgeDocument();

        document.setDocId(docId);
        document.setTitle(title);
        document.setContent(content);
        document.setSourcePath(filePath);
        document.setVersion("1.0");
        document.setAudience("support");
        document.setUpdatedAt(LocalDateTime.now());

        repository.save(document);

        System.out.println(
                "Loaded: " + docId + " - " + title
        );
    }
}