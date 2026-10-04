package com.trustdesk.controller;

import com.trustdesk.model.AgentContext;
import com.trustdesk.model.TriageResult;
import com.trustdesk.service.AgentContextService;
import com.trustdesk.service.TriageAiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent")
public class TriageAiController {

    private final AgentContextService agentContextService;
    private final TriageAiService triageAiService;

    public TriageAiController(
            AgentContextService agentContextService,
            TriageAiService triageAiService) {

        this.agentContextService = agentContextService;
        this.triageAiService = triageAiService;
    }

    @PostMapping("/triage/{ticketId}")
    public TriageResult triageWithAi(
            @PathVariable String ticketId) {

        AgentContext context =
                agentContextService.buildContext(ticketId);

        return triageAiService.analyzeTicket(context);
    }
}