package com.trustdesk.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustdesk.entity.ToolAction;
import com.trustdesk.model.ToolActionRequest;
import com.trustdesk.model.ToolExecutionResult;
import com.trustdesk.repository.ToolActionRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ToolActionService {

    private final ToolActionRepository toolActionRepository;
    private final ToolExecutorService toolExecutorService;
    private final ObjectMapper objectMapper;

    public ToolActionService(
            ToolActionRepository toolActionRepository,
            ToolExecutorService toolExecutorService,
            ObjectMapper objectMapper) {

        this.toolActionRepository = toolActionRepository;
        this.toolExecutorService = toolExecutorService;
        this.objectMapper = objectMapper;
    }

    public ToolActionRequest createAction(
            String action,
            String ticketId,
            String customerId,
            String orderId,
            String reason,
            String riskLevel,
            List<String> evidence) {

        String idempotencyKey =
                UUID.randomUUID().toString();

        ToolAction toolAction = new ToolAction();

        toolAction.setIdempotencyKey(idempotencyKey);
        toolAction.setAction(action);
        toolAction.setTicketId(ticketId);
        toolAction.setCustomerId(customerId);
        toolAction.setOrderId(orderId);
        toolAction.setReason(reason);
        toolAction.setRiskLevel(riskLevel);
        toolAction.setApprovalStatus("PENDING");
        toolAction.setExecutionStatus("NOT_EXECUTED");
        toolAction.setEvidence(toJson(evidence));
        toolAction.setCreatedAt(OffsetDateTime.now());

        toolActionRepository.save(toolAction);

        return toRequest(toolAction);
    }

    public ToolActionRequest approveAction(
            String idempotencyKey) {

        ToolAction toolAction =
                findAction(idempotencyKey);

        if (!"PENDING".equalsIgnoreCase(
                toolAction.getApprovalStatus())) {

            throw new IllegalStateException(
                    "Action is not pending approval: "
                            + idempotencyKey
            );
        }

        toolAction.setApprovalStatus("APPROVED");
        toolAction.setApprovedAt(OffsetDateTime.now());

        toolActionRepository.save(toolAction);

        return toRequest(toolAction);
    }

    public ToolActionRequest rejectAction(
            String idempotencyKey) {

        ToolAction toolAction =
                findAction(idempotencyKey);

        if (!"PENDING".equalsIgnoreCase(
                toolAction.getApprovalStatus())) {

            throw new IllegalStateException(
                    "Action is not pending approval: "
                            + idempotencyKey
            );
        }

        toolAction.setApprovalStatus("REJECTED");
        toolAction.setExecutionStatus("NOT_EXECUTED");

        toolActionRepository.save(toolAction);

        return toRequest(toolAction);
    }

    public ToolActionRequest getAction(
            String idempotencyKey) {

        return toRequest(findAction(idempotencyKey));
    }

    public ToolExecutionResult executeApprovedAction(
            String idempotencyKey) {

        ToolAction toolAction =
                findAction(idempotencyKey);

        if (!"APPROVED".equalsIgnoreCase(
                toolAction.getApprovalStatus())) {

            throw new IllegalStateException(
                    "Action has not been approved: "
                            + idempotencyKey
            );
        }

        /*
         * Idempotency protection:
         * If this action has already completed,
         * return the stored result instead of executing again.
         */
        if ("SUCCESS".equalsIgnoreCase(
                toolAction.getExecutionStatus())) {

            return new ToolExecutionResult(
                    toolAction.getAction(),
                    "SUCCESS",
                    "Action was already executed successfully.",
                    toolAction.getIdempotencyKey(),
                    null
            );
        }

        ToolActionRequest request =
                toRequest(toolAction);

        ToolExecutionResult result =
                toolExecutorService.execute(request);

        toolAction.setExecutionStatus(
                result.getStatus()
        );

        toolAction.setExecutedAt(
                OffsetDateTime.now()
        );

        toolActionRepository.save(toolAction);

        return result;
    }

    private ToolAction findAction(
            String idempotencyKey) {

        return toolActionRepository
                .findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Action not found: "
                                        + idempotencyKey
                        )
                );
    }

    private ToolActionRequest toRequest(
            ToolAction action) {

        ToolActionRequest request =
                new ToolActionRequest();

        request.setAction(action.getAction());
        request.setTicketId(action.getTicketId());
        request.setCustomerId(action.getCustomerId());
        request.setOrderId(action.getOrderId());
        request.setReason(action.getReason());
        request.setRiskLevel(action.getRiskLevel());
        request.setApprovalStatus(
                action.getApprovalStatus()
        );
        request.setExecutionStatus(
                action.getExecutionStatus()
        );
        request.setIdempotencyKey(
                action.getIdempotencyKey()
        );
        request.setEvidence(
                fromJson(action.getEvidence())
        );

        return request;
    }

    private String toJson(List<String> evidence) {

        if (evidence == null) {
            return "[]";
        }

        try {
            return objectMapper.writeValueAsString(
                    evidence
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Failed to store action evidence.",
                    e
            );
        }
    }

    private List<String> fromJson(String evidence) {

        if (evidence == null || evidence.isBlank()) {
            return List.of();
        }

        try {
            return objectMapper.readValue(
                    evidence,
                    objectMapper.getTypeFactory()
                            .constructCollectionType(
                                    List.class,
                                    String.class
                            )
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Failed to read action evidence.",
                    e
            );
        }
    }
}