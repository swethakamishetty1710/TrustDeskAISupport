package com.trustdesk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustdesk.model.ToolActionRequest;
import com.trustdesk.repository.ToolActionRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ToolActionServiceTest {

    @Test
    void shouldBlockExecutionWhenActionIsNotApproved() {

        ToolActionRepository repository =
                mock(ToolActionRepository.class);

        ToolExecutorService executor =
                mock(ToolExecutorService.class);

        ObjectMapper objectMapper =
                new ObjectMapper();

        ToolActionService service =
                new ToolActionService(
                        repository,
                        executor,
                        objectMapper
                );

        ToolActionRequest action =
                new ToolActionRequest();

        action.setIdempotencyKey("test-key");
        action.setApprovalStatus("PENDING");

        when(repository.findByIdempotencyKey("test-key"))
                .thenReturn(java.util.Optional.of(
                        createPendingAction()
                ));

        assertThrows(
                IllegalStateException.class,
                () -> service.executeApprovedAction("test-key")
        );

        verify(executor, never()).execute(any());
    }

    private com.trustdesk.entity.ToolAction createPendingAction() {

        com.trustdesk.entity.ToolAction action =
                new com.trustdesk.entity.ToolAction();

        action.setIdempotencyKey("test-key");
        action.setAction("create_replacement_order");
        action.setTicketId("tkt_9001");
        action.setApprovalStatus("PENDING");
        action.setExecutionStatus("NOT_EXECUTED");

        return action;
    }
}