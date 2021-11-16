package com.microsoft.graph.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import okhttp3.Call;
import okhttp3.Response;

class CoreHttpCallbackFutureWrapperTests {

    @Test
    void throwsIfCallIsNull() {
        assertThrows(NullPointerException.class, () -> new CoreHttpCallbackFutureWrapper(null));
    }
    boolean isCanceled = false;

    @Test
    void cancelsCall() {
        var call = mock(Call.class);
        doAnswer(i -> {
            isCanceled = true;
            return null;
        }).when(call).cancel();
        var wrapper = new CoreHttpCallbackFutureWrapper(call);
        wrapper.future.cancel(true);
        assertTrue(isCanceled);
    }

    @Test
    void returnsResponseWhenCompleted() throws IOException, InterruptedException, ExecutionException {
        var call = mock(Call.class);
        var response = mock(Response.class);
        var wrapper = new CoreHttpCallbackFutureWrapper(call);
        wrapper.onResponse(call, response);
        assertEquals(response, wrapper.future.get());
    }

}
