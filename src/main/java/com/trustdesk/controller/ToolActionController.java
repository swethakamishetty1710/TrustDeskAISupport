package com.trustdesk.controller;

import com.trustdesk.model.ToolActionRequest;
import com.trustdesk.service.ToolActionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/actions")
public class ToolActionController {

    private final ToolActionService toolActionService;

    public ToolActionController(
            ToolActionService toolActionService) {

        this.toolActionService = toolActionService;
    }

    /**
     * Create a new sensitive action request.
     */
    @PostMapping
    public ResponseEntity<ToolActionRequest> createAction(
            @RequestBody ToolActionRequest request) {

        ToolActionRequest createdAction = toolActionService.createAction(
                request.getAction(),
                request.getTicketId(),
                request.getCustomerId(),
                request.getOrderId(),
                request.getReason(),
                request.getRiskLevel(),
                request.getEvidence()
        );

        return ResponseEntity.ok(createdAction);
    }

    /**
     * Approve a sensitive action.
     */
    @PostMapping("/{idempotencyKey}/approve")
    public ResponseEntity<?> approveAction(
            @PathVariable String idempotencyKey) {

        try {

            ToolActionRequest request =
                    toolActionService.approveAction(
                            idempotencyKey
                    );

            return ResponseEntity.ok(request);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .notFound()
                    .build();

        } catch (IllegalStateException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    /**
     * Reject a sensitive action.
     */
    @PostMapping("/{idempotencyKey}/reject")
    public ResponseEntity<?> rejectAction(
            @PathVariable String idempotencyKey) {

        try {

            ToolActionRequest request =
                    toolActionService.rejectAction(
                            idempotencyKey
                    );

            return ResponseEntity.ok(request);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .notFound()
                    .build();

        } catch (IllegalStateException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    /**
     * Get an existing action.
     */
    @GetMapping("/{idempotencyKey}")
    public ResponseEntity<?> getAction(
            @PathVariable String idempotencyKey) {

        try {

            ToolActionRequest request =
                    toolActionService.getAction(
                            idempotencyKey
                    );

            return ResponseEntity.ok(request);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }
}