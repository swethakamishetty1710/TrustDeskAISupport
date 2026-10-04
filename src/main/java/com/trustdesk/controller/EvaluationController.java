package com.trustdesk.controller;

import com.trustdesk.service.EvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/evaluation")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(
            EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping("/run")
    public ResponseEntity<?> runEvaluation() {

        evaluationService.runTriageEvaluationAsync();

        return ResponseEntity.accepted().body(
                Map.of(
                        "status", "RUNNING",
                        "message", "Evaluation started in background"
                )
        );
    }

    @GetMapping("/status")
    public ResponseEntity<?> getEvaluationStatus() {

        String status = evaluationService.getEvaluationStatus();

        if ("COMPLETED".equals(status)) {

            return ResponseEntity.ok(
                    evaluationService.getLatestEvaluationResult()
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "status", status
                )
        );
    }
}