package com.trustdesk.service;

import com.trustdesk.model.ToolActionRequest;
import com.trustdesk.model.ToolExecutionResult;
import com.trustdesk.repository.OrderRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class ToolExecutorServiceTest {

    @Test
    void shouldReturnSameResultWhenSameIdempotencyKeyIsExecutedTwice() {

        OrderRepository orderRepository =
                mock(OrderRepository.class);

        ToolExecutorService service =
                new ToolExecutorService(orderRepository);

        ToolActionRequest action =
                new ToolActionRequest();

        action.setAction("unsupported_test_action");
        action.setApprovalStatus("APPROVED");
        action.setIdempotencyKey("idem-test-001");

        ToolExecutionResult firstResult =
                service.execute(action);

        ToolExecutionResult secondResult =
                service.execute(action);

        assertSame(firstResult, secondResult);

        verifyNoInteractions(orderRepository);
    }
}