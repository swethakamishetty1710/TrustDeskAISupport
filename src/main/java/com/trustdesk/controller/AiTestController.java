package com.trustdesk.controller;

import com.trustdesk.service.AiTestService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiTestController {

    private final AiTestService aiTestService;

    public AiTestController(AiTestService aiTestService) {
        this.aiTestService = aiTestService;
    }

    @GetMapping("/test")
    public String testAi(
            @RequestParam String message) {

        return aiTestService.ask(message);
    }
}