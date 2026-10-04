package com.trustdesk.controller;

import com.trustdesk.model.ToolActionRequest;
import com.trustdesk.service.AgentActionService;
import com.trustdesk.service.AgentExecutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent")
public class AgentActionController {

    private final AgentActionService agentActionService;
    private final AgentExecutionService agentExecutionService;

    public AgentActionController(
            AgentActionService agentActionService,
            AgentExecutionService agentExecutionService) {

        this.agentActionService = agentActionService;
        this.agentExecutionService = agentExecutionService;
    }

    @PostMapping("/action/{ticketId}")
    public ResponseEntity<?> createActionForTicket(
            @PathVariable String ticketId) {

        try {
            ToolActionRequest request =
                    agentActionService.createActionForTicket(ticketId);

            return ResponseEntity.ok(request);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/execute/{idempotencyKey}")
    public ResponseEntity<?> executeApprovedAction(
            @PathVariable String idempotencyKey) {

        try {

            return ResponseEntity.ok(
                    agentExecutionService.executeApprovedAction(
                            idempotencyKey
                    )
            );

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
}