package com.trustdesk.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustdesk.entity.AiTrace;
import com.trustdesk.repository.AiTraceRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class AiTraceService {

    private final AiTraceRepository aiTraceRepository;
    private final ObjectMapper objectMapper;

    public AiTraceService(
            AiTraceRepository aiTraceRepository,
            ObjectMapper objectMapper) {

        this.aiTraceRepository = aiTraceRepository;
        this.objectMapper = objectMapper;
    }

    public AiTrace saveTrace(
            String ticketId,
            String runType,
            List<String> retrievedDocumentIds,
            List<String> toolActions,
            String guardrailResult,
            String finalStatus) {

        AiTrace trace = new AiTrace();

        trace.setTicketId(ticketId);
        trace.setRunType(runType);
        trace.setRetrievedDocumentIds(
                toJson(retrievedDocumentIds)
        );
        trace.setToolActions(
                toJson(toolActions)
        );
        trace.setGuardrailResult(guardrailResult);
        trace.setFinalStatus(finalStatus);
        trace.setCreatedAt(OffsetDateTime.now());

        return aiTraceRepository.save(trace);
    }

    public List<AiTrace> getTracesForTicket(
            String ticketId) {

        return aiTraceRepository
                .findByTicketIdOrderByCreatedAtDesc(ticketId);
    }

    private String toJson(List<String> values) {

        if (values == null) {
            return "[]";
        }

        try {
            return objectMapper.writeValueAsString(values);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Failed to serialize AI trace data.",
                    e
            );
        }
    }
}