package com.microsoft.graph.http;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Wraps the HTTP execution in a future, not public by intention
 */
class CoreHttpCallbackFutureWrapper implements Callback {
    public CoreHttpCallbackFutureWrapper(@Nonnull final Call call) {
        Objects.requireNonNull(call);
        future.whenComplete((r, ex) -> {
            if (ex != null && (ex instanceof InterruptedException || ex instanceof CancellationException)) {
                call.cancel();
            }
        });
    }
    final CompletableFuture<Response> future = new CompletableFuture<>();
	@Override
	public void onFailure(Call arg0, IOException arg1) {
		future.completeExceptionally(arg1);
	}

	@Override
	public void onResponse(Call arg0, Response arg1) throws IOException {
		future.complete(arg1);
	}

}
