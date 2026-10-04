package com.trustdesk.service;

import com.trustdesk.entity.Order;
import com.trustdesk.entity.OrderItem;
import com.trustdesk.model.ToolActionRequest;
import com.trustdesk.model.ToolExecutionResult;
import com.trustdesk.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ToolExecutorService {

    private final OrderRepository orderRepository;

    private final Map<String, ToolExecutionResult> executionResults =
            new ConcurrentHashMap<>();

    public ToolExecutorService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public ToolExecutionResult execute(ToolActionRequest action) {

        /*
         * Safety gate:
         * Tools must never execute unless a human
         * has explicitly approved the action.
         */
        if (!"APPROVED".equalsIgnoreCase(action.getApprovalStatus())) {

            return new ToolExecutionResult(
                    action.getAction(),
                    "BLOCKED",
                    "Tool execution blocked because human approval has not been granted.",
                    action.getIdempotencyKey(),
                    null
            );
        }

        /*
         * Idempotency:
         * If this exact action was already executed,
         * return the previous result instead of
         * performing the side effect again.
         */
        if (executionResults.containsKey(action.getIdempotencyKey())) {
            return executionResults.get(action.getIdempotencyKey());
        }

        ToolExecutionResult result;

        switch (action.getAction()) {

            case "create_replacement_order":
                result = createReplacementOrder(action);
                break;

            default:
                result = new ToolExecutionResult(
                        action.getAction(),
                        "UNSUPPORTED",
                        "Tool execution is not implemented yet for action: "
                                + action.getAction(),
                        action.getIdempotencyKey(),
                        null
                );
        }

        /*
         * Store the result using the idempotency key.
         */
        executionResults.put(
                action.getIdempotencyKey(),
                result
        );

        return result;
    }

    private ToolExecutionResult createReplacementOrder(
            ToolActionRequest action) {

        if (action.getOrderId() == null ||
                action.getOrderId().isBlank()) {

            return new ToolExecutionResult(
                    action.getAction(),
                    "FAILED",
                    "Replacement order cannot be created because no order was provided.",
                    action.getIdempotencyKey(),
                    null
            );
        }

        Order originalOrder =
                orderRepository.findById(action.getOrderId())
                        .orElse(null);

        if (originalOrder == null) {

            return new ToolExecutionResult(
                    action.getAction(),
                    "FAILED",
                    "Original order was not found: "
                            + action.getOrderId(),
                    action.getIdempotencyKey(),
                    null
            );
        }

        /*
         * Create a new replacement order.
         */
        Order replacementOrder = new Order();

        replacementOrder.setOrderId(
                generateReplacementOrderId()
        );

        replacementOrder.setCustomerId(
                originalOrder.getCustomerId()
        );

        replacementOrder.setStatus(
                "replacement_requested"
        );

        replacementOrder.setPlacedAt(
                java.time.LocalDate.now()
        );

        replacementOrder.setTotal(
                originalOrder.getTotal()
        );

        replacementOrder.setCurrency(
                originalOrder.getCurrency()
        );

        replacementOrder.setPaymentStatus(
                "not_required"
        );

        replacementOrder.setTrackingNumber(
                null
        );

        /*
         * Copy the original order items.
         */
        for (OrderItem originalItem :
                originalOrder.getItems()) {

            OrderItem replacementItem =
                    new OrderItem();

            replacementItem.setSku(
                    originalItem.getSku()
            );

            replacementItem.setName(
                    originalItem.getName()
            );

            replacementItem.setQuantity(
                    originalItem.getQuantity()
            );

            replacementItem.setCategory(
                    originalItem.getCategory()
            );

            replacementItem.setFinalSale(
                    originalItem.isFinalSale()
            );

            replacementItem.setOrder(
                    replacementOrder
            );

            replacementOrder.getItems()
                    .add(replacementItem);
        }

        orderRepository.save(replacementOrder);

        return new ToolExecutionResult(
                action.getAction(),
                "SUCCESS",
                "Replacement order created successfully.",
                action.getIdempotencyKey(),
                replacementOrder.getOrderId()
        );
    }

    private String generateReplacementOrderId() {

        return "rpl_" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8);
    }
}