package com.trustdesk.controller;

import com.trustdesk.model.AgentContext;
import com.trustdesk.service.AgentContextService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent")
public class AgentContextController {

    private final AgentContextService agentContextService;

    public AgentContextController(
            AgentContextService agentContextService) {
        this.agentContextService = agentContextService;
    }

    @GetMapping("/context/{ticketId}")
    public AgentContext getAgentContext(
            @PathVariable String ticketId) {

        return agentContextService.buildContext(ticketId);
    }
}