package com.trustdesk.service;

import com.trustdesk.model.AgentExecutionResult;
import com.trustdesk.model.ToolActionRequest;
import com.trustdesk.model.ToolExecutionResult;
import org.springframework.stereotype.Service;

@Service
public class AgentExecutionService {

    private final ToolActionService toolActionService;

    public AgentExecutionService(
            ToolActionService toolActionService) {

        this.toolActionService = toolActionService;
    }

    public AgentExecutionResult executeApprovedAction(
            String idempotencyKey) {

        ToolActionRequest action =
                toolActionService.getAction(idempotencyKey);

        /*
         * The agent cannot execute an action
         * unless a human has approved it.
         */
        if (!"APPROVED".equalsIgnoreCase(
                action.getApprovalStatus())) {

            return new AgentExecutionResult(
                    action.getTicketId(),
                    action.getAction(),
                    "BLOCKED",
                    "The action has not been approved by a human.",
                    null,
                    "I could not execute the requested action because human approval is required."
            );
        }

        /*
         * The ToolActionService already owns the
         * actual execution and idempotency logic.
         */
        ToolExecutionResult toolResult =
                toolActionService.executeApprovedAction(
                        idempotencyKey
                );

        /*
         * Agent observes the tool result.
         */
        String agentResponse;

        if ("SUCCESS".equalsIgnoreCase(
                toolResult.getStatus())) {

            agentResponse =
                    buildSuccessResponse(
                            action,
                            toolResult
                    );

        } else {

            agentResponse =
                    buildFailureResponse(
                            action,
                            toolResult
                    );
        }

        return new AgentExecutionResult(
                action.getTicketId(),
                action.getAction(),
                toolResult.getStatus(),
                toolResult.getMessage(),
                toolResult.getCreatedResourceId(),
                agentResponse
        );
    }

    private String buildSuccessResponse(
            ToolActionRequest action,
            ToolExecutionResult result) {

        if ("create_replacement_order"
                .equalsIgnoreCase(action.getAction())) {

            return "The replacement order has been successfully created. "
                    + "Replacement order ID: "
                    + result.getCreatedResourceId()
                    + ".";
        }

        return "The requested action was completed successfully.";
    }

    private String buildFailureResponse(
            ToolActionRequest action,
            ToolExecutionResult result) {

        return "The requested action could not be completed. "
                + result.getMessage();
    }
}