package com.trustdesk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustdesk.entity.Customer;
import com.trustdesk.entity.KnowledgeDocument;
import com.trustdesk.entity.Order;
import com.trustdesk.entity.Ticket;
import com.trustdesk.model.AgentContext;
import com.trustdesk.model.EvalCase;
import com.trustdesk.model.EvalResult;
import com.trustdesk.model.EvalSummary;
import com.trustdesk.model.TriageResult;
import com.trustdesk.repository.CustomerRepository;
import com.trustdesk.repository.OrderRepository;
import com.trustdesk.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import java.util.concurrent.CompletableFuture;

@Service
public class EvaluationService {

    private final ObjectMapper objectMapper;
    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final TriageAiService triageAiService;
    private final KnowledgeSearchService knowledgeSearchService;
    private volatile String evaluationStatus = "IDLE";

    private volatile EvalSummary latestEvaluationResult;

    public EvaluationService(
            ObjectMapper objectMapper,
            TicketRepository ticketRepository,
            CustomerRepository customerRepository,
            OrderRepository orderRepository,
            TriageAiService triageAiService,
            KnowledgeSearchService knowledgeSearchService) {

        this.objectMapper = objectMapper;
        this.ticketRepository = ticketRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.triageAiService = triageAiService;
        this.knowledgeSearchService = knowledgeSearchService;
    }
    @Async
    public CompletableFuture<EvalSummary> runTriageEvaluationAsync() {

        EvalSummary summary = runTriageEvaluation();

        return CompletableFuture.completedFuture(summary);
    }
    public EvalSummary runTriageEvaluation() {
        evaluationStatus = "RUNNING";

        List<EvalResult> results = new ArrayList<>();

        List<EvalCase> evalCases = loadEvalCases();

        for (EvalCase evalCase : evalCases) {

            Ticket ticket = ticketRepository
                    .findById(evalCase.getTicketId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Ticket not found: "
                                            + evalCase.getTicketId()
                            )
                    );

            Customer customer = customerRepository
                    .findById(ticket.getCustomerId())
                    .orElse(null);

            Order order = null;

            if (ticket.getOrderId() != null
                    && !ticket.getOrderId().isBlank()) {

                order = orderRepository
                        .findById(ticket.getOrderId())
                        .orElse(null);
            }

            AgentContext context = new AgentContext();

            context.setTicket(ticket);
            context.setCustomer(customer);
            context.setOrder(order);

            /*
             * TriageAiService expects knowledge document IDs
             * as part of AgentContext.
             *
             * For this first evaluation step we are measuring
             * triage accuracy only.
             *
             * Citation evaluation will be added separately.
             */
            String query =
                    ticket.getSubject() + " " + ticket.getBody();

            List<String> knowledgeDocumentIds =
                    knowledgeSearchService
                            .vectorSearch(query)
                            .stream()
                            .map(KnowledgeDocument::getDocId)
                            .toList();

            context.setKnowledgeDocumentIds(
                    knowledgeDocumentIds
            );

            TriageResult actual =
                    triageAiService.analyzeTicket(context);

            EvalResult result = new EvalResult();

            result.setCaseId(evalCase.getCaseId());
            result.setTicketId(evalCase.getTicketId());

            result.setActualCategory(
                    actual.getCategory()
            );

            result.setActualPriority(
                    actual.getPriority()
            );

            result.setActualEscalation(
                    actual.isEscalationRequired()
            );

            result.setCategoryCorrect(
                    equalsIgnoreCase(
                            evalCase.getExpectedCategory(),
                            actual.getCategory()
                    )
            );

            result.setPriorityCorrect(
                    equalsIgnoreCase(
                            evalCase.getExpectedPriority(),
                            actual.getPriority()
                    )
            );

            result.setEscalationCorrect(
                    evalCase.isExpectedEscalation()
                            == actual.isEscalationRequired()
            );

            /*
             * Citation and unsafe-action evaluation
             * will be implemented in later steps.
             */
            result.setExpectedCitations(
                    evalCase.getExpectedCitations()
            );

            result.setActualCitations(
                    new ArrayList<>()
            );

            result.setCitationCoverage(0.0);

            result.setUnsafeActionExpected(
                    evalCase.isUnsafeActionExpected()
            );

            result.setUnsafeActionBlocked(false);

            results.add(result);
        }
        EvalSummary summary = buildSummary(results);

        latestEvaluationResult = summary;
        evaluationStatus = "COMPLETED";

        return summary;
    }

    private List<EvalCase> loadEvalCases() {

        List<EvalCase> cases = new ArrayList<>();

        try {

            InputStream inputStream =
                    getClass()
                            .getClassLoader()
                            .getResourceAsStream(
                                    "eval_cases.jsonl"
                            );

            if (inputStream == null) {

                throw new IllegalStateException(
                        "eval_cases.jsonl not found in classpath"
                );
            }

            try (BufferedReader reader =
                         new BufferedReader(
                                 new InputStreamReader(
                                         inputStream,
                                         StandardCharsets.UTF_8
                                 )
                         )) {

                String line;

                while ((line = reader.readLine()) != null) {

                    if (line.isBlank()) {
                        continue;
                    }

                    EvalCase evalCase =
                            objectMapper.readValue(
                                    line,
                                    EvalCase.class
                            );

                    cases.add(evalCase);
                }
            }

        } catch (Exception e) {

        throw new IllegalStateException(
                "Failed to load eval_cases.jsonl: " + e.getMessage(),
                e
        );
    }

        return cases;
    }
    public String getEvaluationStatus() {
        return evaluationStatus;
    }

    public EvalSummary getLatestEvaluationResult() {
        return latestEvaluationResult;
    }

    public void setEvaluationStatus(String status) {
        this.evaluationStatus = status;
    }
    private EvalSummary buildSummary(
            List<EvalResult> results) {

        EvalSummary summary = new EvalSummary();

        int totalCases = results.size();

        int categoryCorrect = 0;
        int priorityCorrect = 0;
        int escalationCorrect = 0;

        for (EvalResult result : results) {

            if (result.isCategoryCorrect()) {
                categoryCorrect++;
            }

            if (result.isPriorityCorrect()) {
                priorityCorrect++;
            }

            if (result.isEscalationCorrect()) {
                escalationCorrect++;
            }
        }

        summary.setTotalCases(totalCases);

        summary.setCategoryCorrect(
                categoryCorrect
        );

        summary.setPriorityCorrect(
                priorityCorrect
        );

        summary.setEscalationCorrect(
                escalationCorrect
        );

        if (totalCases > 0) {

            summary.setTriageAccuracy(
                    (double) categoryCorrect
                            / totalCases
            );

            summary.setEscalationAccuracy(
                    (double) escalationCorrect
                            / totalCases
            );

        } else {

            summary.setTriageAccuracy(0.0);
            summary.setEscalationAccuracy(0.0);
        }

        /*
         * These metrics are intentionally zero for now.
         * We will implement them in later steps.
         */
        summary.setCitationCoverage(0.0);

        summary.setUnsafeActionBlockRate(0.0);

        summary.setResults(results);

        return summary;
    }

    private boolean equalsIgnoreCase(
            String expected,
            String actual) {

        if (expected == null || actual == null) {
            return expected == actual;
        }

        return expected.equalsIgnoreCase(actual);
    }
}