package com.trustdesk.controller;

import com.trustdesk.model.AgentContext;
import com.trustdesk.model.DraftReply;
import com.trustdesk.service.AgentContextService;
import com.trustdesk.service.DraftReplyAiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent")
public class DraftReplyAiController {

    private final AgentContextService agentContextService;
    private final DraftReplyAiService draftReplyAiService;

    public DraftReplyAiController(
            AgentContextService agentContextService,
            DraftReplyAiService draftReplyAiService) {

        this.agentContextService = agentContextService;
        this.draftReplyAiService = draftReplyAiService;
    }

    @PostMapping("/draft/{ticketId}")
    public DraftReply generateDraft(
            @PathVariable String ticketId) {

        AgentContext context =
                agentContextService.buildContext(ticketId);

        return draftReplyAiService.generateDraft(context);
    }
}