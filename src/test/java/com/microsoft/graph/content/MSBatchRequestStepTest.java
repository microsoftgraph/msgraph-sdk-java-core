package com.microsoft.graph.content;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import okhttp3.Request;

public class MSBatchRequestStepTest {

    @Test
    public void testMSBatchRequestStepCreation() {
        Request request = new Request.Builder().url("http://graph.microsoft.com").build();
        MSBatchRequestStep requestStep = new MSBatchRequestStep("1", request);
        assertTrue(requestStep != null, "Test BatchRequestStep creation");
        assertTrue(requestStep.getRequestId().compareTo("1") == 0, "Test Request id");
        assertTrue(requestStep.getRequest() == request, "Test Request object");
        assertTrue(requestStep.getDependsOnIds() != null, "Test Array of depends on Ids");
    }

    @Test
    public void defensiveProgrammingTests() {
        assertThrows(NullPointerException.class, () -> {
            new MSBatchRequestStep(null, null);
        }, "should throw argument exception");
        assertThrows(NullPointerException.class, () -> {
            new MSBatchRequestStep("id", null);
        }, "should throw argument exception");
        assertThrows(IllegalArgumentException.class, () -> {
            new MSBatchRequestStep("", null);
        }, "should throw argument exception");
        new MSBatchRequestStep("id", mock(Request.class));
    }

}
