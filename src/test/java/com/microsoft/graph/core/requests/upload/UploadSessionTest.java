package com.microsoft.graph.core.requests.upload;

import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.microsoft.graph.core.models.UploadSession;

class UploadSessionTest {
    @Test
    void getNextExpectedRangesDoesNotFailOnDefault()
    {
        final UploadSession uploadSession = new UploadSession();
        final List<String> result = uploadSession.getNextExpectedRanges();
        assertNull(result);
    }
}
