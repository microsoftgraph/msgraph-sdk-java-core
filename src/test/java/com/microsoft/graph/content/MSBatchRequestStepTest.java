package com.microsoft.graph.content;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import okhttp3.Request;

public class MSBatchRequestStepTest {

    @Test
    public void testMSBatchRequestStepCreation() {
        Request request = new Request.Builder().url("http://graph.microsoft.com").build();
        MSBatchRequestStep requestStep = new MSBatchRequestStep("1", request);
        assertTrue("Test BatchRequestStep creation", requestStep != null);
        assertTrue("Test Request id", requestStep.getRequestId().compareTo("1") == 0);
        assertTrue("Test Request object", requestStep.getRequest() == request);
        assertTrue("Test Array of depends on Ids", requestStep.getDependsOnIds() != null);
    }

    @Test
    public void defensiveProgrammingTests() {
        assertThrows("should throw argument exception", IllegalArgumentException.class, () -> {
            new MSBatchRequestStep(null, null);
        });
        assertThrows("should throw argument exception", IllegalArgumentException.class, () -> {
            new MSBatchRequestStep("id", null);
        });
        assertThrows("should throw argument exception", IllegalArgumentException.class, () -> {
            new MSBatchRequestStep("", null);
        });
        new MSBatchRequestStep("id", mock(Request.class));
    }

}
