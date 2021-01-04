package com.microsoft.graph.content;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import okhttp3.Request;

public class MSBatchRequestStepTest {

    @Test
    public void testMSBatchRequestStepCreation() {
        Request request = new Request.Builder().url("http://graph.microsoft.com").build();
        List<String> arrayOfDependsOnIds = new ArrayList<>();
        MSBatchRequestStep requestStep = new MSBatchRequestStep("1", request, arrayOfDependsOnIds);
        assertTrue("Test BatchRequestStep creation", requestStep != null);
        assertTrue("Test Request id", requestStep.getRequestId().compareTo("1") == 0);
        assertTrue("Test Request object", requestStep.getRequest() == request);
        assertTrue("Test Array of depends on Ids", requestStep.getArrayOfDependsOnIds() != null);
    }

}
